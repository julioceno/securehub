package com.securehub.auth.adapters.in.controller;

import com.securehub.auth.adapters.in.dto.SignInDTO;
import com.securehub.auth.application.dto.AuthRequestDTO;
import com.securehub.auth.application.dto.AuthResponse;
import com.securehub.auth.application.usecases.auth.AuthenticateUserUseCase;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthenticateUserUseCase authenticateUserUseCase;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse httpResponse;

    private AuthController authController;

    @BeforeEach
    void setUp() {
        authController = new AuthController(authenticateUserUseCase, request, httpResponse);
    }

    @Test
    void createUser_ShouldReturnNoContent_WhenAuthenticationIsSuccessful() {
        SignInDTO signInDTO = new SignInDTO("test@email.com", "password123");
        String requestURL = "http://localhost:8080/v1/auth";
        String requestURI = "/v1/auth";
        String baseUrl = "http://localhost:8080";
        String token = "jwt-token-123";

        AuthResponse authResponse = new AuthResponse(token);

        when(request.getRequestURL()).thenReturn(new StringBuffer(requestURL));
        when(request.getRequestURI()).thenReturn(requestURI);
        when(authenticateUserUseCase.run(any(AuthRequestDTO.class))).thenReturn(authResponse);

        var response = authController.createUser(signInDTO);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());

        verify(authenticateUserUseCase).run(argThat(authRequest ->
                authRequest.email().equals("test@email.com") &&
                        authRequest.password().equals("password123") &&
                        authRequest.baseUrl().equals(baseUrl)
        ));

        verify(httpResponse).addCookie(argThat(cookie ->
                cookie.getName().equals("token") &&
                        cookie.getValue().equals(token) &&
                        cookie.isHttpOnly() &&
                        cookie.getPath().equals("/")
        ));
    }

    @Test
    void me_ShouldReturnOkWithMessage() {
        ResponseEntity<String> response = authController.me();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("oioi", response.getBody());
    }
}