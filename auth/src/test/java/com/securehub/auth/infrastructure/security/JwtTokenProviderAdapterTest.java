package com.securehub.auth.infrastructure.security;

import com.securehub.auth.application.exception.UnauthorizedException;
import com.securehub.auth.domain.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JwtTokenProviderAdapterTest {

    private JwtTokenProviderAdapter tokenProvider;
    private User user;

    @BeforeEach
    void setUp() {
        tokenProvider = new JwtTokenProviderAdapter("test-secret-key", 3600, "test-issuer");
        user = new User(
                "9774360b-f32d-4a29-9e4d-8f0f4fb662b3",
                "username",
                "test@example.com",
                "hashedPassword",
                true
            );
    }

    @Test
    void shouldValidateTokenAndReturnSubject() {
        String token = tokenProvider.createToken(user);
        String subject = tokenProvider.validateToken(token);

        assertEquals(user.getEmail(), subject);
    }

    @Test
    void shouldReturnNullForInvalidToken() {
        String invalidToken = "invalid.token.here";
        String subject = tokenProvider.validateToken(invalidToken);

        assertNull(subject);
    }

    @Test
    void shouldReturnNullForMalformedToken() {
        String malformedToken = "malformed-token";
        String subject = tokenProvider.validateToken(malformedToken);

        assertNull(subject);
    }

    @Test
    void shouldReturnNullForExpiredToken() {
        JwtTokenProviderAdapter expiredTokenProvider = new JwtTokenProviderAdapter("test-secret", -1, "test-issuer");
        String expiredToken = expiredTokenProvider.createToken(user);

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        String subject = tokenProvider.validateToken(expiredToken);

        assertNull(subject);
    }

    @Test
    void shouldCreateDifferentTokensForSameUser() {
        String token1 = tokenProvider.createToken(user);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        String token2 = tokenProvider.createToken(user);

        assertNotEquals(token1, token2);
    }

    @Test
    void shouldValidateTokenFromDifferentProviderWithSameSecret() {
        JwtTokenProviderAdapter anotherProvider = new JwtTokenProviderAdapter("test-secret-key", 3600, "test-issuer");
        String token = anotherProvider.createToken(user);

        String subject = tokenProvider.validateToken(token);

        assertEquals(user.getEmail(), subject);
    }

    @Test
    void shouldReturnNullForTokenFromDifferentSecret() {
        JwtTokenProviderAdapter differentSecretProvider = new JwtTokenProviderAdapter("different-secret", 3600, "test-issuer");
        String token = differentSecretProvider.createToken(user);

        String subject = tokenProvider.validateToken(token);

        assertNull(subject);
    }

    @Test
    void shouldReturnNullForTokenFromDifferentIssuer() {
        JwtTokenProviderAdapter differentIssuerProvider = new JwtTokenProviderAdapter("test-secret-key", 3600, "different-issuer");
        String token = differentIssuerProvider.createToken(user);

        String subject = tokenProvider.validateToken(token);

        assertNull(subject);
    }

    @Test
    void shouldThrowUnauthorizedExceptionWhenSecretIsNull() {
        JwtTokenProviderAdapter nullSecretProvider = new JwtTokenProviderAdapter(null, 3600, "test-issuer");

        assertThrows(UnauthorizedException.class, () -> nullSecretProvider.createToken(user));
    }

    @Test
    void shouldHandleEmptyToken() {
        String subject = tokenProvider.validateToken("");

        assertNull(subject);
    }

    @Test
    void shouldHandleNullToken() {
        String subject = tokenProvider.validateToken(null);

        assertNull(subject);
    }
}