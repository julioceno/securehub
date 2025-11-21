package com.securehub.auth.application.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class GenerateCodeTest {

    @Test
    void generateCode_withLength_returnsNumericStringOfGivenLength() {
        int length = 8;
        String code = GenerateCode.generateCode(length);

        assertNotNull(code);
        assertEquals(length, code.length());
        assertTrue(code.matches("\\d+"), "Should contain only digits");
    }

    @Test
    void generateCode_default_returnsSixDigits() {
        String code = GenerateCode.generateCode();

        assertNotNull(code);
        assertEquals(6, code.length());
        assertTrue(code.matches("\\d+"), "Default code should contain only digits");
    }

    @Test
    void generateCode_isNotAlwaysTheSame() {
        int runs = 5;
        java.util.Set<String> set = new java.util.HashSet<>();
        for (int i = 0; i < runs; i++) {
            set.add(GenerateCode.generateCode());
        }
        assertTrue(set.size() > 1, "At least two different codes are expected in multiple generations");
    }
}