package com.securehub.auth.application.service.user;

import com.securehub.auth.application.exception.BadRequestException;
import com.securehub.auth.application.exception.NotFoundException;
import com.securehub.auth.application.mapper.UserMapper;
import com.securehub.auth.application.port.out.TokenEncryptorPort;
import com.securehub.auth.application.usecases.user.ActivateUserUseCase;
import com.securehub.auth.application.util.CorrelationId;
import com.securehub.auth.domain.activationCode.ActivationCode;
import com.securehub.auth.domain.activationCode.ActivationCodeRepositoryPort;
import com.securehub.auth.domain.user.User;
import com.securehub.auth.domain.user.UserDTO;
import com.securehub.auth.domain.user.UserRepositoryPort;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;

public class ActivateUserServiceImpl implements ActivateUserUseCase {

    private static final Logger log = LoggerFactory.getLogger(ActivateUserServiceImpl.class);

    private final UserRepositoryPort userRepositoryPort;
    private final ActivationCodeRepositoryPort activationCodeRepositoryPort;
    private final UserMapper userMapper;
    private final TokenEncryptorPort tokenEncryptorPort;

    public ActivateUserServiceImpl(UserRepositoryPort userRepositoryPort, ActivationCodeRepositoryPort activationCodeRepositoryPort, UserMapper userMapper, TokenEncryptorPort tokenEncryptorPort) {
        this.userRepositoryPort = userRepositoryPort;
        this.activationCodeRepositoryPort = activationCodeRepositoryPort;
        this.userMapper = userMapper;
        this.tokenEncryptorPort = tokenEncryptorPort;
    }

    @Override
    @Transactional
    public UserDTO run(String email, String code) {
        String correlationId = CorrelationId.get();
        log.info("ActivateUserServiceImpl.run - start - correlationId [{}] - email [{}]", correlationId, email);

        User user = getUser(email);
        ActivationCode activationCode = getActivationCode(user.getId());
        validateActivationCode(code, activationCode.getCode());

        Instant now = Instant.now();
        log.debug("ActivateUserServiceImpl.run - correlationId [{}] - activationCodeId [{}] - now [{}]", correlationId, activationCode.getId(), now);
        activationCode.setConfirmedAt(now);
        activationCodeRepositoryPort.save(activationCode);
        log.debug("ActivateUserServiceImpl.run - correlationId [{}] - activationCodeId [{}] - now [{}]", correlationId, activationCode.getId(), now);

        user.setEnabled(true);
        userRepositoryPort.save(user);
        log.info("ActivateUserServiceImpl.run - end - correlationId [{}] - email [{}]", correlationId, email);
        return userMapper.toDto(user);
    }

    private User getUser(String email) {
        String correlationId = CorrelationId.get();
        log.debug("ActivateUserServiceImpl.getUser - start - correlationId [{}] - email [{}]", correlationId, email);

        User user = userRepositoryPort.findByEmail(email)
                .orElseThrow(() -> {
                    log.warn("ActivateUserServiceImpl.getUser - user not found - correlationId [{}] - email [{}]", correlationId, email);
                    return new NotFoundException("User not found");
                });

        if (user.getEnabled()) {
            log.warn("ActivateUserServiceImpl.getUser - user already enabled - correlationId [{}] - email [{}]", correlationId, email);
            throw new BadRequestException("User is already enabled");
        }

        log.debug("ActivateUserServiceImpl.getUser - end - correlationId [{}] - email [{}]", correlationId, email);
        return user;
    }

    private ActivationCode getActivationCode(String userId) {
        String correlationId = CorrelationId.get();
        log.debug("ActivateUserServiceImpl.getActivationCode - start - correlationId [{}] - userId [{}]", correlationId, userId);

        ActivationCode activationCode = activationCodeRepositoryPort
                .findByUserIdAndConfirmedAtIsNullAndDeletedAtIsNull(userId)
                .orElseThrow(() -> {
                    log.error("ActivateUserServiceImpl.getActivationCode - Activation code not found - correlationId [{}] - userId [{}]", correlationId, userId);
                    return new NotFoundException("Activation code not found");
                });

        if (activationCode.getConfirmedAt() != null) {
            log.error("ActivateUserServiceImpl.getActivationCode - Activation code is already confirmed - correlationId [{}] - userId [{}]", correlationId, userId);
            throw new BadRequestException("Activation code is already confirmed");
        }

        if (activationCode.getDeletedAt() != null) {
            log.error("ActivateUserServiceImpl.getActivationCode - Activation code is invalid - correlationId [{}] - userId [{}]", correlationId, userId);
            throw new BadRequestException("Activation code is invalid");
        }

        if (activationCode.getExpiresAt().isBefore(Instant.now())) {
            log.error("ActivateUserServiceImpl.getActivationCode - Activation code expired - correlationId [{}] - userId [{}]", correlationId, userId);
            throw new BadRequestException("Activation code expired");
        }

        log.debug("ActivateUserServiceImpl.getActivationCode - start - correlationId [{}] - userId [{}]", correlationId, userId);
        return activationCode;
    }

    private void validateActivationCode(String rawCode, String encryptedCode) {
        String correlationId = CorrelationId.get();
        log.debug("ActivateUserServiceImpl.validateActivationCode - start - correlationId [{}]", correlationId);
        boolean isValid = tokenEncryptorPort.compare(rawCode, encryptedCode);

        if (!isValid) {
            log.error("ActivateUserServiceImpl.validateActivationCode - invalid activation code - correlationId [{}]", correlationId);
            throw new BadRequestException("Invalid activation code");
        }

        log.debug("ActivateUserServiceImpl.validateActivationCode - end - correlationId [{}]", correlationId);
    }
}
