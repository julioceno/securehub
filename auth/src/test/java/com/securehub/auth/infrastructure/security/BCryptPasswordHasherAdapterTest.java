package com.securehub.auth.infrastructure.security;

import com.securehub.auth.application.port.out.PasswordHasher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BCryptPasswordHasherAdapterTest {

    private PasswordHasher passwordHasher;

    @BeforeEach
    void setUp() {
        passwordHasher = new BCryptPasswordHasherAdapter();
    }

    @Test
    void shouldHashPassword() {
        String rawPassword = "myPassword123";

        String hashedPassword = passwordHasher.hash(rawPassword);

        assertNotNull(hashedPassword);
        assertNotEquals(rawPassword, hashedPassword);
        assertTrue(hashedPassword.startsWith("$2a$"));
    }

    @Test
    void shouldMatchCorrectPassword() {
        String rawPassword = "correctPassword";
        String hashedPassword = passwordHasher.hash(rawPassword);

        boolean matches = passwordHasher.matches(rawPassword, hashedPassword);

        assertTrue(matches);
    }

    @Test
    void shouldNotMatchIncorrectPassword() {
        String rawPassword = "correctPassword";
        String wrongPassword = "wrongPassword";
        String hashedPassword = passwordHasher.hash(rawPassword);

        boolean matches = passwordHasher.matches(wrongPassword, hashedPassword);

        assertFalse(matches);
    }

    @Test
    void shouldProduceDifferentHashesForSamePassword() {
        String rawPassword = "samePassword";

        String hash1 = passwordHasher.hash(rawPassword);
        String hash2 = passwordHasher.hash(rawPassword);

        assertNotEquals(hash1, hash2);
        assertTrue(passwordHasher.matches(rawPassword, hash1));
        assertTrue(passwordHasher.matches(rawPassword, hash2));
    }

    @Test
    void shouldHandleEmptyPassword() {
        String emptyPassword = "";

        String hashedPassword = passwordHasher.hash(emptyPassword);

        assertNotNull(hashedPassword);
        assertTrue(passwordHasher.matches(emptyPassword, hashedPassword));
    }

    @Test
    void shouldHandleSpecialCharacters() {
        String specialPassword = "!@#$%^&*()_+{}|:<>?";

        String hashedPassword = passwordHasher.hash(specialPassword);

        assertNotNull(hashedPassword);
        assertTrue(passwordHasher.matches(specialPassword, hashedPassword));
    }
}