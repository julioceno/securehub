package com.securehub.auth.application.service.user;

import com.securehub.auth.application.port.out.PasswordHasher;
import com.securehub.auth.application.port.out.SignerPort;
import com.securehub.auth.domain.passwordResetToken.PasswordResetToken;
import com.securehub.auth.domain.passwordResetToken.RequestPasswordResetTokenDTO;
import com.securehub.auth.domain.user.User;
import com.securehub.auth.domain.user.UserRepositoryPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ResetPasswordServiceImplTest {
    @Mock
    private UserRepositoryPort userRepository;

    @Mock
    private com.securehub.auth.domain.passwordResetToken.PasswordResetTokenRepositoryPort passwordResetTokenRepositoryPort;

    @Mock
    private PasswordHasher passwordHasher;

    @Mock
    private SignerPort signerPort;

    @InjectMocks
    private ResetPasswordServiceImpl resetPasswordService;

    @Test
    void shouldResetPasswordSuccessfully() {
        User user = spy(new User("userId", "username", "test@securehub.com", "old", true));

        when(userRepository.findByEmail("test@securehub.com")).thenReturn(Optional.of(user));

        PasswordResetToken token = new PasswordResetToken("tokenId", "userId", "tokenValue", Instant.now().plusSeconds(3600), null, null);
        when(passwordResetTokenRepositoryPort.findByUserIdAndConfirmedAtIsNullAndDeletedAtIsNull("userId")).thenReturn(Optional.of(token));

        when(signerPort.compare("tokenValue", token.getToken())).thenReturn(true);
        when(passwordHasher.hash("newPass")).thenReturn("hashedPass");

        RequestPasswordResetTokenDTO dto = new RequestPasswordResetTokenDTO("test@securehub.com", "tokenValue", "newPass");

        resetPasswordService.run(dto);

        verify(passwordHasher).hash("newPass");
        verify(user).setPassword("hashedPass");
        verify(userRepository).save(user);

        ArgumentCaptor<PasswordResetToken> captor = ArgumentCaptor.forClass(PasswordResetToken.class);
        verify(passwordResetTokenRepositoryPort).save(captor.capture());
        assertNotNull(captor.getValue().getConfirmedAt());
    }

    @Test
    void shouldNotReset_When_TokenInvalid() {
        User user = new User("userId", "username", "test@securehub.com", "old", true);

        when(userRepository.findByEmail("test@securehub.com")).thenReturn(Optional.of(user));

        PasswordResetToken token = new PasswordResetToken("tokenId", "userId", "tokenValue", Instant.now().plusSeconds(3600), null, null);
        when(passwordResetTokenRepositoryPort.findByUserIdAndConfirmedAtIsNullAndDeletedAtIsNull("userId")).thenReturn(Optional.of(token));

        when(signerPort.compare("badToken", token.getToken())).thenReturn(false);

        RequestPasswordResetTokenDTO dto = new RequestPasswordResetTokenDTO("test@securehub.com", "badToken", "newPass");

        resetPasswordService.run(dto);

        verify(passwordHasher, never()).hash(anyString());
        verify(userRepository, never()).save(any());
        verify(passwordResetTokenRepositoryPort, never()).save(any());
    }
}

