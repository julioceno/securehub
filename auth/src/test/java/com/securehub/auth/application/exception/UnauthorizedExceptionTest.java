package com.securehub.auth.application.exception;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class UnauthorizedExceptionTest {

    @Test
    void shouldCreateExceptionWithMessage() {
        String message = "Unauthorized access";

        UnauthorizedException exception = new UnauthorizedException(message);

        assertEquals(message, exception.getMessage());
    }

    @Test
    void shouldInheritFromRuntimeException() {
        UnauthorizedException exception = new UnauthorizedException("test");

        assertInstanceOf(RuntimeException.class, exception);
    }
}