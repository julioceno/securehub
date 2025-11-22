package com.securehub.auth.application.usecases.auth;

import com.securehub.auth.application.dto.AuthRequestDTO;
import com.securehub.auth.application.dto.AuthResponse;

public interface AuthenticateUserUseCase {
    AuthResponse run(AuthRequestDTO request);
}
