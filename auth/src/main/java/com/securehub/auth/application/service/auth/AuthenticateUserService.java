package com.securehub.auth.application.service.auth;

import com.securehub.auth.application.dto.AuthRequest;
import com.securehub.auth.application.dto.AuthResponse;
import com.securehub.auth.application.exception.UnauthorizedException;
import com.securehub.auth.application.port.out.PasswordHasher;
import com.securehub.auth.application.port.out.TokenProviderPort;
import com.securehub.auth.application.usecases.auth.AuthenticateUserUseCase;
import com.securehub.auth.domain.user.User;
import com.securehub.auth.domain.user.UserRepositoryPort;

public class AuthenticateUserService implements AuthenticateUserUseCase {
    private final UserRepositoryPort userRepositoryPort;
    private final PasswordHasher passwordEncoder;
    private final TokenProviderPort tokenProviderPort;

    public AuthenticateUserService(
            UserRepositoryPort userRepositoryPort,
            PasswordHasher passwordEncoder,
            TokenProviderPort tokenProviderPort) {
        this.userRepositoryPort = userRepositoryPort;
        this.passwordEncoder = passwordEncoder;
        this.tokenProviderPort = tokenProviderPort;
    }

    @Override
    public AuthResponse authenticate(AuthRequest request) {
        User user = userRepositoryPort.findByEmail(request.email())
                .orElseThrow(() -> new UnauthorizedException("Invalid credentials"));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new UnauthorizedException("Invalid credentials");
        }

        String token = tokenProviderPort.createToken(user);
        return new AuthResponse(token);
    }
}
