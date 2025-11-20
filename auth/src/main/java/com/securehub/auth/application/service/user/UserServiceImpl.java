package com.securehub.auth.application.service.user;

import com.securehub.auth.application.usecases.user.*;
import com.securehub.auth.domain.passwordResetToken.RequestPasswordResetTokenDTO;
import com.securehub.auth.domain.user.User;
import com.securehub.auth.domain.user.UserDTO;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserUseCases {
    private final CreateUserUseCases createUserUseCase;
    private final ActivateUserUseCase activateUserUseCase;
    private final ForgotPasswordUseCase forgotPasswordUseCase;
    private final ResetPasswordUseCase resetPasswordUseCase;

    public UserServiceImpl(
            CreateUserUseCases createUserUseCase,
            ActivateUserUseCase activateUserUseCase,
            ForgotPasswordUseCase forgotPasswordUseCase,
            ResetPasswordUseCase resetPasswordUseCase
    ) {
        this.createUserUseCase = createUserUseCase;
        this.activateUserUseCase = activateUserUseCase;
        this.forgotPasswordUseCase = forgotPasswordUseCase;
        this.resetPasswordUseCase = resetPasswordUseCase;
    }

    @Override
    public UserDTO createUser(User user) {
        return createUserUseCase.run(user);
    }

    @Override
    public void enableUser(String email, String code) {
        activateUserUseCase.run(email, code);
    }

    @Override
    public void forgotPassword(String email) {
        forgotPasswordUseCase.run(email);
    }

    public void resetPassword(RequestPasswordResetTokenDTO dto) {
        resetPasswordUseCase.run(dto);
    }
}
