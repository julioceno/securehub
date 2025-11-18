package com.securehub.auth.application.service.auth;

import com.securehub.auth.application.dto.AuthRequest;
import com.securehub.auth.application.dto.AuthResponse;
import com.securehub.auth.application.exception.UnauthorizedException;
import com.securehub.auth.application.port.out.PasswordHasher;
import com.securehub.auth.application.port.out.TokenProviderPort;
import com.securehub.auth.application.usecases.auth.AuthenticateUserUseCase;
import com.securehub.auth.application.util.CorrelationId;
import com.securehub.auth.domain.user.User;
import com.securehub.auth.domain.user.UserRepositoryPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AuthenticateUserService implements AuthenticateUserUseCase {
    private static final Logger log = LoggerFactory.getLogger(AuthenticateUserService.class);

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
        String correlationId = CorrelationId.get();
        log.info("AuthenticateUserService.authenticate - start - correlationId [{}] - email [{}]", correlationId, request.email());

        User user = userRepositoryPort.findByEmail(request.email()).orElseThrow(() -> {
            log.warn("AuthenticateUserService.authenticate - userNotFound - correlationId [{}] - email [{}]", correlationId, request.email());
            return new UnauthorizedException("Invalid credentials");
        });

        log.debug("AuthenticateUserService.authenticate - userFound - correlationId [{}] - id [{}] - email [{}] - enabled [{}]",
                correlationId, user.getId(), user.getEmail(), user.getEnabled());

        if (!user.getEnabled()) {
            log.warn("AuthenticateUserService.authenticate - userDisabled - correlationId [{}] - id [{}] - email [{}]", correlationId, user.getId(), user.getEmail());
            throw new UnauthorizedException("User is disabled");
        }

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            log.warn("AuthenticateUserService.authenticate - invalidPassword - correlationId [{}] - email [{}]", correlationId, request.email());
            throw new UnauthorizedException("Invalid credentials");
        }

        String token = tokenProviderPort.createToken(user);
        log.info("AuthenticateUserService.authenticate - end - correlationId [{}] - id [{}] - email [{}]", correlationId, user.getId(), user.getEmail());
        return new AuthResponse(token);
    }
}
