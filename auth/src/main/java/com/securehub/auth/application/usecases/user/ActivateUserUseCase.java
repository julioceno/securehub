package com.securehub.auth.application.usecases.user;

import com.securehub.auth.domain.user.UserDTO;

public interface ActivateUserUseCase {
    UserDTO run(String email, String code);
}