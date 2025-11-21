package com.securehub.auth.infrastructure.security;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class HmacSignerAdapterTest {

    @Test
    void encrypt_returns_64_hex_characters() throws Exception {
        HmacSignerAdapter adapter = new HmacSignerAdapter("test-secret");
        String token = "some-token";

        String hmac = adapter.encrypt(token);

        assertNotNull(hmac);
        assertEquals(64, hmac.length(), "HMAC SHA-256 must produce 64 hex characters");
    }

    @Test
    void compare_returns_true_for_matching_hmac_and_false_for_others_and_nulls() throws Exception {
        String secret = "another-secret";
        HmacSignerAdapter adapter = new HmacSignerAdapter(secret);

        String token = "user-token";
        String hmac = adapter.encrypt(token);

        assertTrue(adapter.compare(token, hmac));
        assertFalse(adapter.compare("different-token", hmac));
        assertFalse(adapter.compare(token, "deadbeef"));
        assertFalse(adapter.compare(null, hmac));
        assertFalse(adapter.compare(token, null));

        HmacSignerAdapter adapterWithNullSecret = new HmacSignerAdapter(null);
        assertFalse(adapterWithNullSecret.compare(token, hmac));
    }
}

