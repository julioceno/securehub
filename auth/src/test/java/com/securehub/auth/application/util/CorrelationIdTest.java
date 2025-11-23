package com.securehub.auth.application.util;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;

import static org.junit.jupiter.api.Assertions.*;

class CorrelationIdTest {

    @BeforeEach
    void setUp() {
        MDC.clear();
    }

    @AfterEach
    void tearDown() {
        MDC.clear();
    }

    @Test
    void shouldReturnCorrectHeaderName() {
        assertEquals("X-Correlation-Id", CorrelationId.HEADER_NAME);
    }

    @Test
    void shouldReturnNull_When_CorrelationIdNotSet() {
        assertNull(CorrelationId.get());
    }

    @Test
    void shouldReturnCorrelationIdFromMDC() {
        String correlationId = "test-correlation-123";
        MDC.put(CorrelationId.HEADER_NAME, correlationId);

        assertEquals(correlationId, CorrelationId.get());
    }

    @Test
    void shouldReturnNullAfterCorrelationIdIsRemoved() {
        String correlationId = "test-correlation-456";
        MDC.put(CorrelationId.HEADER_NAME, correlationId);
        assertEquals(correlationId, CorrelationId.get());

        MDC.remove(CorrelationId.HEADER_NAME);
        assertNull(CorrelationId.get());
    }

    @Test
    void shouldReturnEmptyStringWhenSetToEmpty() {
        MDC.put(CorrelationId.HEADER_NAME, "");

        assertEquals("", CorrelationId.get());
    }
}