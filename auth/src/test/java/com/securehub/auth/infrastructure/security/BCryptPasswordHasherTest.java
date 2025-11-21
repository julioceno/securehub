package com.securehub.auth.infrastructure.security;

import com.securehub.auth.application.port.out.PasswordHasher;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BCryptPasswordHasherTest {

    private final PasswordHasher hasher = new BCryptPasswordHasher();

    @Test
    void shouldHashAndMatchPassword() {
        String raw = "mySecret123";

        String hashed = hasher.hash(raw);

        assertNotNull(hashed);
        assertNotEquals(raw, hashed);
        assertTrue(hasher.matches(raw, hashed));
    }

    @Test
    void shouldProduceDifferentHashesForSamePassword() {
        String raw = "anotherSecret";

        String h1 = hasher.hash(raw);
        String h2 = hasher.hash(raw);

        assertNotNull(h1);
        assertNotNull(h2);
        assertNotEquals(h1, h2);
        assertTrue(hasher.matches(raw, h1));
        assertTrue(hasher.matches(raw, h2));
    }

    @Test
    void shouldNotMatchDifferentPassword() {
        String raw = "pass1";
        String wrong = "pass2";

        String hashed = hasher.hash(raw);

        assertFalse(hasher.matches(wrong, hashed));
    }
}

