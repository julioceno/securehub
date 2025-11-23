package com.securehub.auth.application.service.user;

import com.securehub.auth.application.port.out.PasswordHasher;
import com.securehub.auth.application.port.out.SignerPort;
import com.securehub.auth.application.util.CorrelationId;
import com.securehub.auth.domain.passwordResetToken.PasswordResetToken;
import com.securehub.auth.domain.passwordResetToken.PasswordResetTokenRepositoryPort;
import com.securehub.auth.domain.passwordResetToken.RequestPasswordResetTokenDTO;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ResetPasswordServiceImplTest {

    @Mock
    private UserRepositoryPort userRepositoryPort;

    @Mock
    private PasswordResetTokenRepositoryPort passwordResetTokenRepositoryPort;

    @Mock
    private PasswordHasher passwordHasher;

    @Mock
    private SignerPort signerPort;

    @InjectMocks
    private ResetPasswordServiceImpl resetPasswordService;

    private User enabledUser;
    private User disabledUser;
    private PasswordResetToken validToken;
    private RequestPasswordResetTokenDTO validRequest;

    @BeforeEach
    void setUp() {
        enabledUser = spy(new User("userId", "username", "test@example.com", "oldPassword", true));
        disabledUser = new User("userId2", "username2", "disabled@example.com", "password", false);
        validToken = new PasswordResetToken("tokenId", "userId", "encryptedToken",
                Instant.now().plus(1, ChronoUnit.HOURS), null, null);
        validRequest = new RequestPasswordResetTokenDTO("test@example.com", "rawToken", "newPassword");
    }

    @Test
    void shouldResetPasswordSuccessfully() {
        String correlationId = "correlation-123";

        try (MockedStatic<CorrelationId> correlationIdMock = mockStatic(CorrelationId.class)) {
            correlationIdMock.when(CorrelationId::get).thenReturn(correlationId);

            when(userRepositoryPort.findByEmail("test@example.com")).thenReturn(Optional.of(enabledUser));
            when(passwordResetTokenRepositoryPort.findByUserIdAndConfirmedAtIsNullAndDeletedAtIsNull("userId"))
                    .thenReturn(Optional.of(validToken));
            when(signerPort.compare("rawToken", "encryptedToken")).thenReturn(true);
            when(passwordHasher.hash("newPassword")).thenReturn("hashedNewPassword");

            resetPasswordService.run(validRequest);

            verify(enabledUser).setPassword("hashedNewPassword");
            verify(userRepositoryPort).save(enabledUser);

            ArgumentCaptor<PasswordResetToken> tokenCaptor = ArgumentCaptor.forClass(PasswordResetToken.class);
            verify(passwordResetTokenRepositoryPort).save(tokenCaptor.capture());

            PasswordResetToken savedToken = tokenCaptor.getValue();
            assertNotNull(savedToken.getConfirmedAt());
        }
    }

    @Test
    void shouldNotReset_When_UserNotFound() {
        String correlationId = "correlation-123";

        try (MockedStatic<CorrelationId> correlationIdMock = mockStatic(CorrelationId.class)) {
            correlationIdMock.when(CorrelationId::get).thenReturn(correlationId);

            when(userRepositoryPort.findByEmail("test@example.com")).thenReturn(Optional.empty());

            resetPasswordService.run(validRequest);

            verify(passwordHasher, never()).hash(anyString());
            verify(userRepositoryPort, never()).save(any());
            verify(passwordResetTokenRepositoryPort, never()).save(any());
        }
    }

    @Test
    void shouldNotReset_When_UserDisabled() {
        String correlationId = "correlation-123";
        RequestPasswordResetTokenDTO request = new RequestPasswordResetTokenDTO("disabled@example.com", "rawToken", "newPassword");

        try (MockedStatic<CorrelationId> correlationIdMock = mockStatic(CorrelationId.class)) {
            correlationIdMock.when(CorrelationId::get).thenReturn(correlationId);

            when(userRepositoryPort.findByEmail("disabled@example.com")).thenReturn(Optional.of(disabledUser));

            resetPasswordService.run(request);

            verify(passwordHasher, never()).hash(anyString());
            verify(userRepositoryPort, never()).save(any());
            verify(passwordResetTokenRepositoryPort, never()).save(any());
        }
    }

    @Test
    void shouldNotReset_When_TokenNotFound() {
        String correlationId = "correlation-123";

        try (MockedStatic<CorrelationId> correlationIdMock = mockStatic(CorrelationId.class)) {
            correlationIdMock.when(CorrelationId::get).thenReturn(correlationId);

            when(userRepositoryPort.findByEmail("test@example.com")).thenReturn(Optional.of(enabledUser));
            when(passwordResetTokenRepositoryPort.findByUserIdAndConfirmedAtIsNullAndDeletedAtIsNull("userId"))
                    .thenReturn(Optional.empty());

            resetPasswordService.run(validRequest);

            verify(passwordHasher, never()).hash(anyString());
            verify(userRepositoryPort, never()).save(any());
            verify(passwordResetTokenRepositoryPort, never()).save(any());
        }
    }

    @Test
    void shouldNotReset_When_TokenExpired() {
        String correlationId = "correlation-123";
        PasswordResetToken expiredToken = new PasswordResetToken("tokenId", "userId", "encryptedToken",
                Instant.now().minus(1, ChronoUnit.HOURS), null, null);

        try (MockedStatic<CorrelationId> correlationIdMock = mockStatic(CorrelationId.class)) {
            correlationIdMock.when(CorrelationId::get).thenReturn(correlationId);

            when(userRepositoryPort.findByEmail("test@example.com")).thenReturn(Optional.of(enabledUser));
            when(passwordResetTokenRepositoryPort.findByUserIdAndConfirmedAtIsNullAndDeletedAtIsNull("userId"))
                    .thenReturn(Optional.of(expiredToken));

            resetPasswordService.run(validRequest);

            verify(passwordHasher, never()).hash(anyString());
            verify(userRepositoryPort, never()).save(any());
            verify(passwordResetTokenRepositoryPort, never()).save(any());
        }
    }

    @Test
    void shouldNotReset_When_TokenAlreadyConfirmed() {
        String correlationId = "correlation-123";
        PasswordResetToken confirmedToken = new PasswordResetToken("tokenId", "userId", "encryptedToken",
                Instant.now().plus(1, ChronoUnit.HOURS), Instant.now(), null);

        try (MockedStatic<CorrelationId> correlationIdMock = mockStatic(CorrelationId.class)) {
            correlationIdMock.when(CorrelationId::get).thenReturn(correlationId);

            when(userRepositoryPort.findByEmail("test@example.com")).thenReturn(Optional.of(enabledUser));
            when(passwordResetTokenRepositoryPort.findByUserIdAndConfirmedAtIsNullAndDeletedAtIsNull("userId"))
                    .thenReturn(Optional.of(confirmedToken));

            resetPasswordService.run(validRequest);

            verify(passwordHasher, never()).hash(anyString());
            verify(userRepositoryPort, never()).save(any());
            verify(passwordResetTokenRepositoryPort, never()).save(any());
        }
    }

    @Test
    void shouldNotReset_When_TokenDeleted() {
        String correlationId = "correlation-123";
        PasswordResetToken deletedToken = new PasswordResetToken("tokenId", "userId", "encryptedToken",
                Instant.now().plus(1, ChronoUnit.HOURS), null, Instant.now());

        try (MockedStatic<CorrelationId> correlationIdMock = mockStatic(CorrelationId.class)) {
            correlationIdMock.when(CorrelationId::get).thenReturn(correlationId);

            when(userRepositoryPort.findByEmail("test@example.com")).thenReturn(Optional.of(enabledUser));
            when(passwordResetTokenRepositoryPort.findByUserIdAndConfirmedAtIsNullAndDeletedAtIsNull("userId"))
                    .thenReturn(Optional.of(deletedToken));

            resetPasswordService.run(validRequest);

            verify(passwordHasher, never()).hash(anyString());
            verify(userRepositoryPort, never()).save(any());
            verify(passwordResetTokenRepositoryPort, never()).save(any());
        }
    }

    @Test
    void shouldNotReset_When_TokenInvalid() {
        String correlationId = "correlation-123";

        try (MockedStatic<CorrelationId> correlationIdMock = mockStatic(CorrelationId.class)) {
            correlationIdMock.when(CorrelationId::get).thenReturn(correlationId);

            when(userRepositoryPort.findByEmail("test@example.com")).thenReturn(Optional.of(enabledUser));
            when(passwordResetTokenRepositoryPort.findByUserIdAndConfirmedAtIsNullAndDeletedAtIsNull("userId"))
                    .thenReturn(Optional.of(validToken));
            when(signerPort.compare("rawToken", "encryptedToken")).thenReturn(false);

            resetPasswordService.run(validRequest);

            verify(passwordHasher, never()).hash(anyString());
            verify(userRepositoryPort, never()).save(any());
            verify(passwordResetTokenRepositoryPort, never()).save(any());
        }
    }
}