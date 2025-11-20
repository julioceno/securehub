package com.securehub.auth.application.usecases.user;

import com.securehub.auth.domain.passwordResetToken.RequestPasswordResetTokenDTO;

public interface ResetPasswordUseCase {
    void run(RequestPasswordResetTokenDTO dto);
}