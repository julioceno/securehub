package com.securehub.auth.application.service.auth;

import com.securehub.auth.application.dto.AuthRequest;
import com.securehub.auth.application.dto.AuthResponse;
import com.securehub.auth.application.exception.UnauthorizedException;
import com.securehub.auth.application.port.out.PasswordHasher;
import com.securehub.auth.application.port.out.TokenProviderPort;
import com.securehub.auth.application.usecases.auth.AuthenticateUserUseCase;
import com.securehub.auth.application.usecases.user.CreateActivateUserCodeUseCase;
import com.securehub.auth.application.util.CorrelationId;
import com.securehub.auth.domain.activationCode.ActivationCode;
import com.securehub.auth.domain.activationCode.ActivationCodeRepositoryPort;
import com.securehub.auth.domain.user.User;
import com.securehub.auth.domain.user.UserRepositoryPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;

public class AuthenticateUserService implements AuthenticateUserUseCase {
    private static final Logger log = LoggerFactory.getLogger(AuthenticateUserService.class);

    private final UserRepositoryPort userRepositoryPort;
    private final PasswordHasher passwordEncoder;
    private final TokenProviderPort tokenProviderPort;
    private final ActivationCodeRepositoryPort  activationCodeRepositoryPort;
    private final CreateActivateUserCodeUseCase createActivateUserCodeUseCase;

    public AuthenticateUserService(
            UserRepositoryPort userRepositoryPort,
            PasswordHasher passwordEncoder,
            TokenProviderPort tokenProviderPort,
            ActivationCodeRepositoryPort  activationCodeRepositoryPort,
            CreateActivateUserCodeUseCase createActivateUserCodeUseCase
    ) {
        this.userRepositoryPort = userRepositoryPort;
        this.passwordEncoder = passwordEncoder;
        this.tokenProviderPort = tokenProviderPort;
        this.createActivateUserCodeUseCase = createActivateUserCodeUseCase;
        this.activationCodeRepositoryPort = activationCodeRepositoryPort;
    }

    @Override
    public AuthResponse authenticate(AuthRequest request) {
        String correlationId = CorrelationId.get();
        log.info("AuthenticateUserService.authenticate - start - correlationId [{}] - email [{}]", correlationId, request.email());

        User user = userRepositoryPort.findByEmail(request.email()).orElseThrow(() -> {
            log.warn("AuthenticateUserService.authenticate - user not found - correlationId [{}] - email [{}]", correlationId, request.email());
            return new UnauthorizedException("Invalid credentials");
        });

        log.debug("AuthenticateUserService.authenticate - user found - correlationId [{}] - id [{}] - email [{}] - enabled [{}]",
                correlationId, user.getId(), user.getEmail(), user.getEnabled());

        if (!user.getEnabled()) {
            shouldSendActivationCode(user);
            log.error("AuthenticateUserService.authenticate - userDisabled - correlationId [{}] - id [{}] - email [{}]", correlationId, user.getId(), user.getEmail());
            throw new UnauthorizedException("User is disabled");
        }

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            log.error("AuthenticateUserService.authenticate - invalidPassword - correlationId [{}] - email [{}]", correlationId, request.email());
            throw new UnauthorizedException("Invalid credentials");
        }

        String token = tokenProviderPort.createToken(user);
        log.info("AuthenticateUserService.authenticate - end - correlationId [{}] - id [{}] - email [{}]", correlationId, user.getId(), user.getEmail());
        return new AuthResponse(token);
    }

    private void shouldSendActivationCode(User user) {
        String correlationId = CorrelationId.get();
        log.debug("AuthenticateUserService.shouldSendActivationCode - start - correlationId [{}] - id [{}] - email [{}]", correlationId, user.getId(), user.getEmail());

        ActivationCode activationCode = activationCodeRepositoryPort.findByUserIdAndConfirmedAtIsNullAndDeletedAtIsNull(user.getId()).orElse(null);
        if (activationCode == null) {
            log.debug("AuthenticateUserService.shouldSendActivationCode - code not exists - correlationId [{}] - id [{}] - email [{}]", correlationId, user.getId(), user.getEmail());
            createActivateUserCodeUseCase.run(user.getId());
            return;
        }

        if (activationCode.getExpiresAt().isBefore(Instant.now())) {
            log.debug("AuthenticateUserService.shouldSendActivationCode - is expired - correlationId [{}] - id [{}] - email [{}]", correlationId, user.getId(), user.getEmail());
            createActivateUserCodeUseCase.run(user.getId());
            return;
        }

        log.debug("AuthenticateUserService.shouldSendActivationCode - end - correlationId [{}] - id [{}] - email [{}]", correlationId, user.getId(), user.getEmail());
    }


}
