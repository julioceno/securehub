package com.securehub.auth.application.service.user;

import com.securehub.auth.application.exception.BadRequestException;
import com.securehub.auth.application.exception.NotFoundException;
import com.securehub.auth.application.mapper.UserMapper;
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

    public ActivateUserServiceImpl(UserRepositoryPort userRepositoryPort, ActivationCodeRepositoryPort activationCodeRepositoryPort, UserMapper userMapper) {
        this.userRepositoryPort = userRepositoryPort;
        this.activationCodeRepositoryPort = activationCodeRepositoryPort;
        this.userMapper = userMapper;
    }

    @Override
    @Transactional
    public UserDTO run(String email, String code) {
        String correlationId = CorrelationId.get();
        log.info("ActivateUserServiceImpl.run - start - correlationId [{}] - email [{}]", correlationId, email);

        User user = userRepositoryPort.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User not found"));

        if (user.getEnabled()) {
            throw new BadRequestException("User is already enabled");
        }

        ActivationCode activationCode = activationCodeRepositoryPort
                .findByUserIdAndCodeAndConfirmedAtIsNull(user.getId(), code)
                .orElseThrow(() -> new NotFoundException("Activation code not found"));

        if (activationCode.getConfirmedAt() != null) {
            throw new BadRequestException("Activation code is already confirmed");
        }

        if (activationCode.getExpiresAt().isBefore(Instant.now())) {
            throw new BadRequestException("Activation code expired");
        }

        activationCode.setConfirmedAt(Instant.now());
        activationCodeRepositoryPort.save(activationCode);

        user.setEnabled(true);
        userRepositoryPort.save(user);
        log.info("ActivateUserServiceImpl.run - end - correlationId [{}] - email [{}]", correlationId, email);
        return userMapper.toDto(user);
    }
}
