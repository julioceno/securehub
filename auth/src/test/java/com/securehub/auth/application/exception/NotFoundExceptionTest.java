package com.securehub.auth.application.exception;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class NotFoundExceptionTest {

    @Test
    void shouldCreateExceptionWithMessage() {
        String message = "Resource not found";

        NotFoundException exception = new NotFoundException(message);

        assertEquals(message, exception.getMessage());
    }

    @Test
    void shouldInheritFromRuntimeException() {
        NotFoundException exception = new NotFoundException("test");

        assertInstanceOf(RuntimeException.class, exception);
    }
}