package com.securehub.auth.application.service.user;

import com.securehub.auth.application.usecases.user.*;
import com.securehub.auth.domain.passwordResetToken.RequestPasswordResetTokenDTO;
import com.securehub.auth.domain.user.User;
import com.securehub.auth.domain.user.UserDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private CreateUserUseCases createUserUseCase;

    @Mock
    private ActivateUserUseCase activateUserUseCase;

    @Mock
    private ForgotPasswordUseCase forgotPasswordUseCase;

    @Mock
    private ResetPasswordUseCase resetPasswordUseCase;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;
    private UserDTO userDTO;
    private RequestPasswordResetTokenDTO resetPasswordRequest;

    @BeforeEach
    void setUp() {
        user = new User("userId", "username", "test@example.com", "password", false);
        userDTO = new UserDTO("userId", "username", "test@example.com");
        resetPasswordRequest = new RequestPasswordResetTokenDTO("test@example.com", "token", "newPassword");
    }

    @Test
    void shouldCreateUserSuccessfully() {
        when(createUserUseCase.run(user)).thenReturn(userDTO);

        UserDTO result = userService.createUser(user);

        assertEquals(userDTO, result);
        verify(createUserUseCase).run(user);
    }

    @Test
    void shouldEnableUserSuccessfully() {
        String email = "test@example.com";
        String code = "123456";

        userService.enableUser(email, code);

        verify(activateUserUseCase).run(email, code);
    }

    @Test
    void shouldCallForgotPasswordSuccessfully() {
        String email = "test@example.com";

        userService.forgotPassword(email);

        verify(forgotPasswordUseCase).run(email);
    }

    @Test
    void shouldResetPasswordSuccessfully() {
        userService.resetPassword(resetPasswordRequest);

        verify(resetPasswordUseCase).run(resetPasswordRequest);
    }

    @Test
    void shouldPassCorrectParametersToCreateUser() {
        when(createUserUseCase.run(user)).thenReturn(userDTO);

        userService.createUser(user);

        verify(createUserUseCase).run(eq(user));
    }

    @Test
    void shouldPassCorrectParametersToEnableUser() {
        String email = "user@test.com";
        String code = "654321";

        userService.enableUser(email, code);

        verify(activateUserUseCase).run(eq(email), eq(code));
    }

    @Test
    void shouldPassCorrectParametersToForgotPassword() {
        String email = "forgot@test.com";

        userService.forgotPassword(email);

        verify(forgotPasswordUseCase).run(eq(email));
    }

    @Test
    void shouldPassCorrectParametersToResetPassword() {
        RequestPasswordResetTokenDTO customRequest = new RequestPasswordResetTokenDTO("custom@test.com", "customToken", "customPassword");

        userService.resetPassword(customRequest);

        verify(resetPasswordUseCase).run(eq(customRequest));
    }
}