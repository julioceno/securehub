package com.securehub.auth.application.service.auth;

import com.securehub.auth.application.dto.SignInDTO;
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
    public AuthResponse run(SignInDTO dto) {
        String correlationId = CorrelationId.get();
        log.info("AuthenticateUserService.run - start - correlationId [{}] - email [{}]", correlationId, dto.email());

        User user = userRepositoryPort.findByEmail(dto.email()).orElseThrow(() -> {
            log.warn("AuthenticateUserService.run - user not found - correlationId [{}] - email [{}]", correlationId, dto.email());
            return new UnauthorizedException("Invalid credentials");
        });

        log.debug("AuthenticateUserService.run - user found - correlationId [{}] - id [{}] - email [{}] - enabled [{}]",
                correlationId, user.getId(), user.getEmail(), user.getEnabled());

        if (!user.getEnabled()) {
            shouldSendActivationCode(user);
            log.error("AuthenticateUserService.run - userDisabled - correlationId [{}] - id [{}] - email [{}]", correlationId, user.getId(), user.getEmail());
            throw new UnauthorizedException("User is disabled");
        }

        if (!passwordEncoder.matches(dto.password(), user.getPassword())) {
            log.error("AuthenticateUserService.run - invalidPassword - correlationId [{}] - email [{}]", correlationId, dto.email());
            throw new UnauthorizedException("Invalid credentials");
        }

        String token = tokenProviderPort.createToken(user);
        log.info("AuthenticateUserService.run - end - correlationId [{}] - id [{}] - email [{}]", correlationId, user.getId(), user.getEmail());
        return new AuthResponse(token);
    }

    private void shouldSendActivationCode(User user) {
        String correlationId = CorrelationId.get();
        log.debug("AuthenticateUserService.shouldSendActivationCode - start - correlationId [{}] - id [{}] - email [{}]",
                correlationId, user.getId(), user.getEmail());

        ActivationCode code = activationCodeRepositoryPort
                .findByUserIdAndConfirmedAtIsNullAndDeletedAtIsNull(user.getId())
                .orElse(null);

        boolean shouldCreateNewCode = code == null || code.getExpiresAt().isBefore(Instant.now());

        if (shouldCreateNewCode) {
            log.debug("AuthenticateUserService.shouldSendActivationCode - creating new code - correlationId [{}] - id [{}] - email [{}]",
                    correlationId, user.getId(), user.getEmail());
            createActivateUserCodeUseCase.run(user);
        }

        log.debug("AuthenticateUserService.shouldSendActivationCode - end - correlationId [{}] - id [{}] - email [{}]",
                correlationId, user.getId(), user.getEmail());
    }
}
