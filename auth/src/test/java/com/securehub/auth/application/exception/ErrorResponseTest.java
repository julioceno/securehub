package com.securehub.auth.application.exception;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDateTime;

class ErrorResponseTest {

    @Test
    void shouldCreateErrorResponseWithAllFields() {
        LocalDateTime timestamp = LocalDateTime.now();
        int status = 400;
        String error = "Bad Request";
        String message = "Error occurred";
        String path = "/api/test";
        String correlationId = "abc-123";

        ErrorResponse errorResponse = new ErrorResponse(timestamp, status, error, message, path, correlationId);

        assertEquals(timestamp, errorResponse.getTimestamp());
        assertEquals(status, errorResponse.getStatus());
        assertEquals(error, errorResponse.getError());
        assertEquals(message, errorResponse.getMessage());
        assertEquals(path, errorResponse.getPath());
        assertEquals(correlationId, errorResponse.getCorrelationId());
    }
}