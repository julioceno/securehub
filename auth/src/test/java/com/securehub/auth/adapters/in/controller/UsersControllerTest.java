package com.securehub.auth.adapters.in.controller;

import com.securehub.auth.adapters.in.dto.EnableUserDTO;
import com.securehub.auth.adapters.in.dto.ForgotPasswordDTO;
import com.securehub.auth.adapters.in.dto.ResetPasswordDTO;
import com.securehub.auth.adapters.in.dto.UserToCreateDTO;
import com.securehub.auth.application.usecases.user.UserUseCases;
import com.securehub.auth.domain.passwordResetToken.RequestPasswordResetTokenDTO;
import com.securehub.auth.domain.user.User;
import com.securehub.auth.domain.user.UserDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsersControllerTest {

    @Mock
    private UserUseCases userUseCases;

    @InjectMocks
    private UsersController usersController;

    @BeforeEach
    void setUp() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/v1/users");
        request.setServerName("localhost");
        request.setServerPort(8080);
        request.setScheme("http");

        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
    }

    @Test
    void createUser_ShouldReturnCreatedWithUserDTO_When_UserIsCreatedSuccessfully() {
        UserToCreateDTO userToCreateDTO = new UserToCreateDTO("testuser", "test@email.com", "password123");
        UserDTO expectedUserDTO = new UserDTO("testuser", "test@email.com", "password123");

        when(userUseCases.createUser(any(User.class))).thenReturn(expectedUserDTO);

        ResponseEntity<UserDTO> response = usersController.createUser(userToCreateDTO);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(expectedUserDTO, response.getBody());
        assertNotNull(response.getHeaders().getLocation());

        verify(userUseCases).createUser(argThat(user ->
                user.getUsername().equals("testuser") &&
                        user.getEmail().equals("test@email.com") &&
                        user.getPassword().equals("password123")
        ));
    }

    @Test
    void createActiveUser_ShouldReturnNoContent_When_UserIsEnabledSuccessfully() {
        EnableUserDTO enableUserDTO = new EnableUserDTO("test@email.com", "123456");

        doNothing().when(userUseCases).enableUser("test@email.com", "123456");

        ResponseEntity<UserDTO> response = usersController.createActiveUser(enableUserDTO);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(userUseCases).enableUser("test@email.com", "123456");
    }

    @Test
    void forgotPassword_ShouldReturnNoContent_When_RequestIsProcessedSuccessfully() {
        ForgotPasswordDTO forgotPasswordDTO = new ForgotPasswordDTO("test@email.com");

        doNothing().when(userUseCases).forgotPassword("test@email.com");

        ResponseEntity response = usersController.forgotPassword(forgotPasswordDTO);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(userUseCases).forgotPassword("test@email.com");
    }

    @Test
    void reset_ShouldReturnNoContent_When_PasswordIsResetSuccessfully() {
        ResetPasswordDTO resetPasswordDTO = new ResetPasswordDTO("test@email.com", "reset-token", "newPassword123");

        doNothing().when(userUseCases).resetPassword(any(RequestPasswordResetTokenDTO.class));

        ResponseEntity<Void> response = usersController.reset(resetPasswordDTO);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(userUseCases).resetPassword(argThat(dto ->
                dto.email().equals("test@email.com") &&
                        dto.token().equals("reset-token") &&
                        dto.newPassword().equals("newPassword123")
        ));
    }
}