package com.securehub.auth.application.exception;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class BadRequestExceptionTest {

    @Test
    void shouldCreateExceptionWithMessage() {
        String message = "Invalid request";

        BadRequestException exception = new BadRequestException(message);

        assertEquals(message, exception.getMessage());
    }

    @Test
    void shouldInheritFromRuntimeException() {
        BadRequestException exception = new BadRequestException("test");

        assertInstanceOf(RuntimeException.class, exception);
    }
}