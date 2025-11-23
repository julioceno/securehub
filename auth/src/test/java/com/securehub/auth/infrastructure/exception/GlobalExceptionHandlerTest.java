package com.securehub.auth.infrastructure.exception;

import com.securehub.auth.application.exception.BadRequestException;
import com.securehub.auth.application.exception.ErrorResponse;
import com.securehub.auth.application.exception.NotFoundException;
import com.securehub.auth.application.exception.UnauthorizedException;
import com.securehub.auth.application.util.CorrelationId;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private MethodArgumentNotValidException methodArgumentNotValidException;

    @Mock
    private BindingResult bindingResult;

    @InjectMocks
    private GlobalExceptionHandler globalExceptionHandler;

    private static final String CORRELATION_ID = "test-correlation-id";
    private static final String REQUEST_URI = "/api/test";

    @BeforeEach
    void setUp() {
        when(request.getRequestURI()).thenReturn(REQUEST_URI);
    }

    @Test
    void handleBadRequest_ShouldReturnBadRequestResponse() {
        BadRequestException exception = new BadRequestException("Bad request message");

        try (MockedStatic<CorrelationId> correlationIdMock = mockStatic(CorrelationId.class)) {
            correlationIdMock.when(CorrelationId::get).thenReturn(CORRELATION_ID);

            ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleBadRequest(exception, request);

            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            ErrorResponse errorResponse = response.getBody();
            assertNotNull(errorResponse);
            assertEquals(400, errorResponse.getStatus());
            assertEquals("Bad Request", errorResponse.getError());
            assertEquals("Bad request message", errorResponse.getMessage());
            assertEquals(REQUEST_URI, errorResponse.getPath());
            assertEquals(CORRELATION_ID, errorResponse.getCorrelationId());
        }
    }

    @Test
    void handleMethodArgumentNotValidException_ShouldReturnValidationErrorResponse() {
        FieldError fieldError1 = new FieldError("object", "field1", "Field1 error message");
        FieldError fieldError2 = new FieldError("object", "field2", "Field2 error message");

        when(methodArgumentNotValidException.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(Arrays.asList(fieldError1, fieldError2));

        try (MockedStatic<CorrelationId> correlationIdMock = mockStatic(CorrelationId.class)) {
            correlationIdMock.when(CorrelationId::get).thenReturn(CORRELATION_ID);

            ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleException(methodArgumentNotValidException, request);

            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            ErrorResponse errorResponse = response.getBody();
            assertNotNull(errorResponse);
            assertEquals(400, errorResponse.getStatus());
            assertEquals("Validation Failed", errorResponse.getError());
            assertEquals("field1: Field1 error message; field2: Field2 error message", errorResponse.getMessage());
            assertEquals(REQUEST_URI, errorResponse.getPath());
            assertEquals(CORRELATION_ID, errorResponse.getCorrelationId());
        }
    }

    @Test
    void handleUnauthorizedException_ShouldReturnUnauthorizedResponse() {
        UnauthorizedException exception = new UnauthorizedException("Unauthorized access");

        try (MockedStatic<CorrelationId> correlationIdMock = mockStatic(CorrelationId.class)) {
            correlationIdMock.when(CorrelationId::get).thenReturn(CORRELATION_ID);

            ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleException(exception, request);

            assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
            ErrorResponse errorResponse = response.getBody();
            assertNotNull(errorResponse);
            assertEquals(401, errorResponse.getStatus());
            assertEquals("Unauthorized", errorResponse.getError());
            assertEquals("Unauthorized access", errorResponse.getMessage());
            assertEquals(REQUEST_URI, errorResponse.getPath());
            assertEquals(CORRELATION_ID, errorResponse.getCorrelationId());
        }
    }

    @Test
    void handleNotFoundException_ShouldReturnNotFoundResponse() {
        NotFoundException exception = new NotFoundException("Resource not found");

        try (MockedStatic<CorrelationId> correlationIdMock = mockStatic(CorrelationId.class)) {
            correlationIdMock.when(CorrelationId::get).thenReturn(CORRELATION_ID);

            ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleException(exception, request);

            assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
            ErrorResponse errorResponse = response.getBody();
            assertNotNull(errorResponse);
            assertEquals(404, errorResponse.getStatus());
            assertEquals("Not Found", errorResponse.getError());
            assertEquals("Resource not found", errorResponse.getMessage());
            assertEquals(REQUEST_URI, errorResponse.getPath());
            assertEquals(CORRELATION_ID, errorResponse.getCorrelationId());
        }
    }

    @Test
    void handleRuntimeException_ShouldReturnInternalServerErrorResponse() {
        RuntimeException exception = new RuntimeException("Unexpected error");

        try (MockedStatic<CorrelationId> correlationIdMock = mockStatic(CorrelationId.class)) {
            correlationIdMock.when(CorrelationId::get).thenReturn(CORRELATION_ID);

            ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleAll(exception, request);

            assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
            ErrorResponse errorResponse = response.getBody();
            assertNotNull(errorResponse);
            assertEquals(500, errorResponse.getStatus());
            assertEquals("Internal Server Error", errorResponse.getError());
            assertEquals("An unexpected error occurred", errorResponse.getMessage());
            assertEquals(REQUEST_URI, errorResponse.getPath());
            assertEquals(CORRELATION_ID, errorResponse.getCorrelationId());
        }
    }
}