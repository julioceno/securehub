package com.securehub.auth.application.service.auth;

import com.securehub.auth.application.dto.SignInDTO;
import com.securehub.auth.application.dto.AuthResponse;
import com.securehub.auth.application.exception.UnauthorizedException;
import com.securehub.auth.application.port.out.PasswordHasher;
import com.securehub.auth.application.port.out.TokenProviderPort;
import com.securehub.auth.application.usecases.user.CreateActivateUserCodeUseCase;
import com.securehub.auth.domain.activationCode.ActivationCode;
import com.securehub.auth.domain.activationCode.ActivationCodeRepositoryPort;
import com.securehub.auth.domain.user.User;
import com.securehub.auth.domain.user.UserRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticateUserServiceTest {

    @Mock
    private UserRepositoryPort userRepositoryPort;

    @Mock
    private PasswordHasher passwordHasher;

    @Mock
    private TokenProviderPort tokenProviderPort;

    @Mock
    private ActivationCodeRepositoryPort activationCodeRepositoryPort;

    @Mock
    private CreateActivateUserCodeUseCase createActivateUserCodeUseCase;

    private AuthenticateUserService authenticateUserService;

    @BeforeEach
    void setUp() {
        authenticateUserService = new AuthenticateUserService(
                userRepositoryPort,
                passwordHasher,
                tokenProviderPort,
                activationCodeRepositoryPort,
                createActivateUserCodeUseCase
        );
    }

    @Test
    void shouldAuthenticateUserSuccessfully() {
        String id = "9774360b-f32d-4a29-9e4d-8f0f4fb662b3";
        String email = "user@example.com";
        String password = "password123";
        String token = "jwt-token";
        SignInDTO authRequest = new SignInDTO(email, password);

        User user = createEnabledUser(id, email);

        when(userRepositoryPort.findByEmail(email)).thenReturn(Optional.of(user));
        when(passwordHasher.matches(password, "hashedPassword")).thenReturn(true);
        when(tokenProviderPort.createToken(user)).thenReturn(token);

        AuthResponse result = authenticateUserService.run(authRequest);

        assertNotNull(result);
        assertEquals(token, result.token());
        verify(userRepositoryPort).findByEmail(email);
        verify(passwordHasher).matches(password, "hashedPassword");
        verify(tokenProviderPort).createToken(user);
    }

    @Test
    void shouldThrowUnauthorizedException_When_UserNotFound() {
        String email = "nonexistent@example.com";
        String password = "password123";
        SignInDTO authRequest = new SignInDTO(email, password);

        when(userRepositoryPort.findByEmail(email)).thenReturn(Optional.empty());

        UnauthorizedException exception = assertThrows(
                UnauthorizedException.class,
                () -> authenticateUserService.run(authRequest)
        );

        assertEquals("Invalid credentials", exception.getMessage());
        verify(userRepositoryPort).findByEmail(email);
        verifyNoInteractions(passwordHasher, tokenProviderPort);
    }

    @Test
    void shouldThrowUnauthorizedException_When_PasswordIsInvalid() {
        String id = "9774360b-f32d-4a29-9e4d-8f0f4fb662b3";
        String email = "user@example.com";
        String password = "wrongPassword";
        SignInDTO authRequest = new SignInDTO(email, password);

        User user = createEnabledUser(id, email);

        when(userRepositoryPort.findByEmail(email)).thenReturn(Optional.of(user));
        when(passwordHasher.matches(password, "hashedPassword")).thenReturn(false);

        UnauthorizedException exception = assertThrows(
                UnauthorizedException.class,
                () -> authenticateUserService.run(authRequest)
        );

        assertEquals("Invalid credentials", exception.getMessage());
        verify(userRepositoryPort).findByEmail(email);
        verify(passwordHasher).matches(password, "hashedPassword");
        verifyNoInteractions(tokenProviderPort);
    }

    @Test
    void shouldThrowUnauthorizedException_When_UserIsDisabledAndNoActivationCodeExists() {
        String id = "9774360b-f32d-4a29-9e4d-8f0f4fb662b3";
        String email = "user@example.com";
        String password = "password123";
        SignInDTO authRequest = new SignInDTO(email, password);

        User user = createDisabledUser(id, email);

        when(userRepositoryPort.findByEmail(email)).thenReturn(Optional.of(user));
        when(activationCodeRepositoryPort.findByUserIdAndConfirmedAtIsNullAndDeletedAtIsNull(id))
                .thenReturn(Optional.empty());

        UnauthorizedException exception = assertThrows(
                UnauthorizedException.class,
                () -> authenticateUserService.run(authRequest)
        );

        assertEquals("User is disabled", exception.getMessage());
        verify(createActivateUserCodeUseCase).run(user);
        verifyNoInteractions(passwordHasher, tokenProviderPort);
    }

    @Test
    void shouldThrowUnauthorizedException_When_UserIsDisabledAndActivationCodeIsExpired() {
        String id = "9774360b-f32d-4a29-9e4d-8f0f4fb662b3";
        String email = "user@example.com";
        String password = "password123";
        SignInDTO authRequest = new SignInDTO(email, password);

        User user = createDisabledUser(id, email);
        ActivationCode expiredCode = createActivationCode(Instant.now().minusSeconds(3600));

        when(userRepositoryPort.findByEmail(email)).thenReturn(Optional.of(user));
        when(activationCodeRepositoryPort.findByUserIdAndConfirmedAtIsNullAndDeletedAtIsNull(id))
                .thenReturn(Optional.of(expiredCode));

        UnauthorizedException exception = assertThrows(
                UnauthorizedException.class,
                () -> authenticateUserService.run(authRequest)
        );

        assertEquals("User is disabled", exception.getMessage());
        verify(createActivateUserCodeUseCase).run(user);
        verifyNoInteractions(passwordHasher, tokenProviderPort);
    }

    @Test
    void shouldThrowUnauthorizedException_When_UserIsDisabledButActivationCodeIsValid() {
        String id = "9774360b-f32d-4a29-9e4d-8f0f4fb662b3";
        String email = "user@example.com";
        String password = "password123";
        SignInDTO authRequest = new SignInDTO(email, password);

        User user = createDisabledUser(id, email);
        ActivationCode validCode = createActivationCode(Instant.now().plusSeconds(3600));

        when(userRepositoryPort.findByEmail(email)).thenReturn(Optional.of(user));
        when(activationCodeRepositoryPort.findByUserIdAndConfirmedAtIsNullAndDeletedAtIsNull(id))
                .thenReturn(Optional.of(validCode));

        UnauthorizedException exception = assertThrows(
                UnauthorizedException.class,
                () -> authenticateUserService.run(authRequest)
        );

        assertEquals("User is disabled", exception.getMessage());
        verify(createActivateUserCodeUseCase, never()).run(any());
        verifyNoInteractions(passwordHasher, tokenProviderPort);
    }

    private User createEnabledUser(String id, String email) {
        User user = mock(User.class);
        when(user.getId()).thenReturn(id);
        when(user.getEmail()).thenReturn(email);
        when(user.getPassword()).thenReturn("hashedPassword");
        when(user.getEnabled()).thenReturn(true);
        return user;
    }

    private User createDisabledUser(String id, String email) {
        User user = mock(User.class);
        when(user.getId()).thenReturn(id);
        when(user.getEmail()).thenReturn(email);
        lenient().when(user.getPassword()).thenReturn("hashedPassword");
        when(user.getEnabled()).thenReturn(false);
        return user;
    }

    private ActivationCode createActivationCode(Instant expiresAt) {
        ActivationCode activationCode = mock(ActivationCode.class);
        when(activationCode.getExpiresAt()).thenReturn(expiresAt);
        return activationCode;
    }
}