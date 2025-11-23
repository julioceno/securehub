package com.securehub.auth.application.service.user;

import com.securehub.auth.application.exception.BadRequestException;
import com.securehub.auth.application.port.out.EmailSenderPort;
import com.securehub.auth.application.port.out.SignerPort;
import com.securehub.auth.application.util.CorrelationId;
import com.securehub.auth.application.util.GenerateCode;
import com.securehub.auth.domain.email.EmailMessage;
import com.securehub.auth.domain.passwordResetToken.PasswordResetToken;
import com.securehub.auth.domain.passwordResetToken.PasswordResetTokenRepositoryPort;
import com.securehub.auth.domain.user.User;
import com.securehub.auth.domain.user.UserRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ForgotPasswordServiceImplTest {

    @Mock
    private UserRepositoryPort userRepositoryPort;

    @Mock
    private PasswordResetTokenRepositoryPort passwordResetTokenRepositoryPort;

    @Mock
    private SignerPort signerPort;

    @Mock
    private EmailSenderPort emailSenderPort;

    @InjectMocks
    private ForgotPasswordServiceImpl forgotPasswordService;

    private User enabledUser;
    private User disabledUser;

    @BeforeEach
    void setUp() {
        enabledUser = new User("userId", "username", "test@example.com", "password", true);
        disabledUser = new User("userId2", "username2", "disabled@example.com", "password", false);
    }

    @Test
    void shouldCreatePasswordResetTokenSuccessfully() throws Exception {
        String email = "test@example.com";
        String rawCode = "123456";
        String encryptedToken = "encryptedToken";
        String correlationId = "correlation-123";

        try (MockedStatic<CorrelationId> correlationIdMock = mockStatic(CorrelationId.class);
             MockedStatic<GenerateCode> generateCodeMock = mockStatic(GenerateCode.class)) {

            correlationIdMock.when(CorrelationId::get).thenReturn(correlationId);
            generateCodeMock.when(GenerateCode::generateCode).thenReturn(rawCode);

            when(userRepositoryPort.findByEmail(email)).thenReturn(Optional.of(enabledUser));
            when(passwordResetTokenRepositoryPort.findByUserIdAndConfirmedAtIsNullAndDeletedAtIsNull("userId"))
                    .thenReturn(Optional.empty());
            when(signerPort.encrypt(rawCode)).thenReturn(encryptedToken);

            forgotPasswordService.run(email);

            ArgumentCaptor<PasswordResetToken> tokenCaptor = ArgumentCaptor.forClass(PasswordResetToken.class);
            verify(passwordResetTokenRepositoryPort).save(tokenCaptor.capture());

            PasswordResetToken savedToken = tokenCaptor.getValue();
            assertEquals("userId", savedToken.getUserId());
            assertEquals(encryptedToken, savedToken.getToken());
            assertNotNull(savedToken.getExpiresAt());
            assertNull(savedToken.getConfirmedAt());
            assertNull(savedToken.getDeletedAt());

            ArgumentCaptor<EmailMessage> emailCaptor = ArgumentCaptor.forClass(EmailMessage.class);
            verify(emailSenderPort).send(emailCaptor.capture());

            EmailMessage emailMessage = emailCaptor.getValue();
            assertEquals("test@example.com", emailMessage.getTo());
            assertEquals("Resetar senha - Código de verificação", emailMessage.getSubject());
            assertEquals("reset-password", emailMessage.getTemplate());
            assertEquals(Map.of("username", "username", "code", rawCode), emailMessage.getParams());
        }
    }

    @Test
    void shouldNotCreateToken_When_UserNotFound() throws Exception {
        String email = "notfound@example.com";
        String correlationId = "correlation-123";

        try (MockedStatic<CorrelationId> correlationIdMock = mockStatic(CorrelationId.class)) {
            correlationIdMock.when(CorrelationId::get).thenReturn(correlationId);

            when(userRepositoryPort.findByEmail(email)).thenReturn(Optional.empty());

            forgotPasswordService.run(email);

            verify(passwordResetTokenRepositoryPort, never()).save(any());
            verify(emailSenderPort, never()).send(any());
        }
    }

    @Test
    void shouldNotCreateToken_When_UserDisabled() throws Exception {
        String email = "disabled@example.com";
        String correlationId = "correlation-123";

        try (MockedStatic<CorrelationId> correlationIdMock = mockStatic(CorrelationId.class)) {
            correlationIdMock.when(CorrelationId::get).thenReturn(correlationId);

            when(userRepositoryPort.findByEmail(email)).thenReturn(Optional.of(disabledUser));

            forgotPasswordService.run(email);

            verify(passwordResetTokenRepositoryPort, never()).save(any());
            verify(emailSenderPort, never()).send(any());
        }
    }

    @Test
    void shouldInvalidateOldToken_When_ExistingTokenFound() throws Exception {
        String email = "test@example.com";
        String rawCode = "123456";
        String encryptedToken = "encryptedToken";
        String correlationId = "correlation-123";

        PasswordResetToken existingToken = new PasswordResetToken("tokenId", "userId", "oldToken",
                Instant.now().plus(10, ChronoUnit.MINUTES), null, null);

        try (MockedStatic<CorrelationId> correlationIdMock = mockStatic(CorrelationId.class);
             MockedStatic<GenerateCode> generateCodeMock = mockStatic(GenerateCode.class)) {

            correlationIdMock.when(CorrelationId::get).thenReturn(correlationId);
            generateCodeMock.when(GenerateCode::generateCode).thenReturn(rawCode);

            when(userRepositoryPort.findByEmail(email)).thenReturn(Optional.of(enabledUser));
            when(passwordResetTokenRepositoryPort.findByUserIdAndConfirmedAtIsNullAndDeletedAtIsNull("userId"))
                    .thenReturn(Optional.of(existingToken));
            when(signerPort.encrypt(rawCode)).thenReturn(encryptedToken);

            forgotPasswordService.run(email);

            assertNotNull(existingToken.getDeletedAt());
            verify(passwordResetTokenRepositoryPort, times(2)).save(any(PasswordResetToken.class));
            verify(emailSenderPort).send(any(EmailMessage.class));
        }
    }

    @Test
    void shouldThrowBadRequestException_When_EncryptionFails() throws Exception {
        String email = "test@example.com";
        String rawCode = "123456";
        String correlationId = "correlation-123";

        try (MockedStatic<CorrelationId> correlationIdMock = mockStatic(CorrelationId.class);
             MockedStatic<GenerateCode> generateCodeMock = mockStatic(GenerateCode.class)) {

            correlationIdMock.when(CorrelationId::get).thenReturn(correlationId);
            generateCodeMock.when(GenerateCode::generateCode).thenReturn(rawCode);

            when(userRepositoryPort.findByEmail(email)).thenReturn(Optional.of(enabledUser));
            when(passwordResetTokenRepositoryPort.findByUserIdAndConfirmedAtIsNullAndDeletedAtIsNull("userId"))
                    .thenReturn(Optional.empty());
            when(signerPort.encrypt(rawCode)).thenThrow(new RuntimeException("Encryption failed"));

            BadRequestException exception = assertThrows(BadRequestException.class,
                    () -> forgotPasswordService.run(email));

            assertEquals("An error occurred while encrypting the token", exception.getMessage());
            verify(passwordResetTokenRepositoryPort, never()).save(any(PasswordResetToken.class));
            verify(emailSenderPort, never()).send(any(EmailMessage.class));
        }
    }

    @Test
    void shouldSetCorrectExpirationTime() throws Exception {
        String email = "test@example.com";
        String rawCode = "123456";
        String encryptedToken = "encryptedToken";
        String correlationId = "correlation-123";
        Instant beforeTest = Instant.now();

        try (MockedStatic<CorrelationId> correlationIdMock = mockStatic(CorrelationId.class);
             MockedStatic<GenerateCode> generateCodeMock = mockStatic(GenerateCode.class)) {

            correlationIdMock.when(CorrelationId::get).thenReturn(correlationId);
            generateCodeMock.when(GenerateCode::generateCode).thenReturn(rawCode);

            when(userRepositoryPort.findByEmail(email)).thenReturn(Optional.of(enabledUser));
            when(passwordResetTokenRepositoryPort.findByUserIdAndConfirmedAtIsNullAndDeletedAtIsNull("userId"))
                    .thenReturn(Optional.empty());
            when(signerPort.encrypt(rawCode)).thenReturn(encryptedToken);

            forgotPasswordService.run(email);

            ArgumentCaptor<PasswordResetToken> tokenCaptor = ArgumentCaptor.forClass(PasswordResetToken.class);
            verify(passwordResetTokenRepositoryPort).save(tokenCaptor.capture());

            PasswordResetToken savedToken = tokenCaptor.getValue();
            Instant expectedMinTime = beforeTest.plus(15, ChronoUnit.MINUTES);
            Instant expectedMaxTime = Instant.now().plus(15, ChronoUnit.MINUTES);

            assertTrue(savedToken.getExpiresAt().isAfter(expectedMinTime.minus(1, ChronoUnit.SECONDS)));
            assertTrue(savedToken.getExpiresAt().isBefore(expectedMaxTime.plus(1, ChronoUnit.SECONDS)));
        }
    }
}