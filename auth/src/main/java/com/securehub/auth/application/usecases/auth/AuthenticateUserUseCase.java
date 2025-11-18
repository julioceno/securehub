package com.securehub.auth.application.usecases.auth;

import com.securehub.auth.application.dto.AuthRequest;
import com.securehub.auth.application.dto.AuthResponse;

public interface AuthenticateUserUseCase {
    AuthResponse authenticate(AuthRequest request);
}
