package com.securehub.auth.application.service.user;

import com.securehub.auth.application.exception.BadRequestException;
import com.securehub.auth.application.port.out.EmailSenderPort;
import com.securehub.auth.application.port.out.SignerPort;
import com.securehub.auth.application.util.CorrelationId;
import com.securehub.auth.application.util.GenerateCode;
import com.securehub.auth.domain.activationCode.ActivationCode;
import com.securehub.auth.domain.activationCode.ActivationCodeRepositoryPort;
import com.securehub.auth.domain.email.EmailMessage;
import com.securehub.auth.domain.user.User;
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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateActivateUserCodeServiceImplTest {

    @Mock
    private ActivationCodeRepositoryPort activationCodeRepositoryPort;

    @Mock
    private SignerPort signerPort;

    @Mock
    private EmailSenderPort emailSenderPort;

    @InjectMocks
    private CreateActivateUserCodeServiceImpl createActivateUserCodeService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User("userId", "username", "test@example.com", "password", false);
    }

    @Test
    void shouldCreateActivationCodeSuccessfully() throws Exception {
        String rawCode = "123456";
        String encryptedCode = "encryptedCode";
        String correlationId = "correlation-123";

        try (MockedStatic<CorrelationId> correlationIdMock = mockStatic(CorrelationId.class);
             MockedStatic<GenerateCode> generateCodeMock = mockStatic(GenerateCode.class)) {

            correlationIdMock.when(CorrelationId::get).thenReturn(correlationId);
            generateCodeMock.when(GenerateCode::generateCode).thenReturn(rawCode);

            when(activationCodeRepositoryPort.findByUserIdAndConfirmedAtIsNullAndDeletedAtIsNull("userId"))
                    .thenReturn(Optional.empty());
            when(signerPort.encrypt(rawCode)).thenReturn(encryptedCode);

            createActivateUserCodeService.run(user);

            ArgumentCaptor<ActivationCode> activationCodeCaptor = ArgumentCaptor.forClass(ActivationCode.class);
            verify(activationCodeRepositoryPort).save(activationCodeCaptor.capture());

            ActivationCode savedCode = activationCodeCaptor.getValue();
            assertEquals("userId", savedCode.getUserId());
            assertEquals(encryptedCode, savedCode.getCode());
            assertNotNull(savedCode.getExpiresAt());
            assertNull(savedCode.getConfirmedAt());
            assertNull(savedCode.getDeletedAt());

            ArgumentCaptor<EmailMessage> emailCaptor = ArgumentCaptor.forClass(EmailMessage.class);
            verify(emailSenderPort).send(emailCaptor.capture());

            EmailMessage emailMessage = emailCaptor.getValue();
            assertEquals("test@example.com", emailMessage.getTo());
            assertEquals("Ative sua conta - Código de verificação", emailMessage.getSubject());
            assertEquals("account-activation", emailMessage.getTemplate());
            assertEquals(Map.of("username", "username", "code", rawCode), emailMessage.getParams());
        }
    }

    @Test
    void shouldInvalidateOldCode_When_ExistingCodeFound() throws Exception  {
        String rawCode = "123456";
        String encryptedCode = "encryptedCode";
        String correlationId = "correlation-123";

        ActivationCode existingCode = new ActivationCode("codeId", "userId", "oldCode",
                Instant.now().plus(10, ChronoUnit.MINUTES), null, null);

        try (MockedStatic<CorrelationId> correlationIdMock = mockStatic(CorrelationId.class);
             MockedStatic<GenerateCode> generateCodeMock = mockStatic(GenerateCode.class)) {

            correlationIdMock.when(CorrelationId::get).thenReturn(correlationId);
            generateCodeMock.when(GenerateCode::generateCode).thenReturn(rawCode);

            when(activationCodeRepositoryPort.findByUserIdAndConfirmedAtIsNullAndDeletedAtIsNull("userId"))
                    .thenReturn(Optional.of(existingCode));
            when(signerPort.encrypt(rawCode)).thenReturn(encryptedCode);

            createActivateUserCodeService.run(user);

            assertNotNull(existingCode.getDeletedAt());
            verify(activationCodeRepositoryPort, times(2)).save(any(ActivationCode.class));
            verify(emailSenderPort).send(any(EmailMessage.class));
        }
    }

    @Test
    void shouldThrowBadRequestException_When_EncryptionFails() throws Exception {
        String rawCode = "123456";
        String correlationId = "correlation-123";

        try (MockedStatic<CorrelationId> correlationIdMock = mockStatic(CorrelationId.class);
             MockedStatic<GenerateCode> generateCodeMock = mockStatic(GenerateCode.class)) {

            correlationIdMock.when(CorrelationId::get).thenReturn(correlationId);
            generateCodeMock.when(GenerateCode::generateCode).thenReturn(rawCode);

            when(activationCodeRepositoryPort.findByUserIdAndConfirmedAtIsNullAndDeletedAtIsNull("userId"))
                    .thenReturn(Optional.empty());
            when(signerPort.encrypt(rawCode)).thenThrow(new RuntimeException("Encryption failed"));

            BadRequestException exception = assertThrows(BadRequestException.class,
                    () -> createActivateUserCodeService.run(user));

            assertEquals("An error occurred while encrypting the code", exception.getMessage());
            verify(activationCodeRepositoryPort, never()).save(any(ActivationCode.class));
            verify(emailSenderPort, never()).send(any(EmailMessage.class));
        }
    }

    @Test
    void shouldSetCorrectExpirationTime() throws Exception {
        String rawCode = "123456";
        String encryptedCode = "encryptedCode";
        String correlationId = "correlation-123";
        Instant beforeTest = Instant.now();

        try (MockedStatic<CorrelationId> correlationIdMock = mockStatic(CorrelationId.class);
             MockedStatic<GenerateCode> generateCodeMock = mockStatic(GenerateCode.class)) {

            correlationIdMock.when(CorrelationId::get).thenReturn(correlationId);
            generateCodeMock.when(GenerateCode::generateCode).thenReturn(rawCode);

            when(activationCodeRepositoryPort.findByUserIdAndConfirmedAtIsNullAndDeletedAtIsNull("userId"))
                    .thenReturn(Optional.empty());
            when(signerPort.encrypt(rawCode)).thenReturn(encryptedCode);

            createActivateUserCodeService.run(user);

            ArgumentCaptor<ActivationCode> activationCodeCaptor = ArgumentCaptor.forClass(ActivationCode.class);
            verify(activationCodeRepositoryPort).save(activationCodeCaptor.capture());

            ActivationCode savedCode = activationCodeCaptor.getValue();
            Instant expectedMinTime = beforeTest.plus(15, ChronoUnit.MINUTES);
            Instant expectedMaxTime = Instant.now().plus(15, ChronoUnit.MINUTES);

            assertTrue(savedCode.getExpiresAt().isAfter(expectedMinTime.minus(1, ChronoUnit.SECONDS)));
            assertTrue(savedCode.getExpiresAt().isBefore(expectedMaxTime.plus(1, ChronoUnit.SECONDS)));
        }
    }
}