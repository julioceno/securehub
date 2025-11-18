package com.securehub.auth.application.service.user;

import com.securehub.auth.application.exception.BadRequestException;
import com.securehub.auth.application.mapper.UserMapper;
import com.securehub.auth.application.port.out.PasswordHasher;
import com.securehub.auth.application.usecases.user.CreateUserUseCases;
import com.securehub.auth.application.util.CorrelationId;
import com.securehub.auth.domain.activationCode.ActivationCode;
import com.securehub.auth.domain.activationCode.ActivationCodeRepositoryPort;
import com.securehub.auth.domain.user.User;
import com.securehub.auth.domain.user.UserDTO;
import com.securehub.auth.domain.user.UserRepositoryPort;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

public class CreateUserServiceImpl implements CreateUserUseCases {
    private static final Logger log = LoggerFactory.getLogger(CreateUserServiceImpl.class);

    private final UserRepositoryPort userRepository;
    private final ActivationCodeRepositoryPort activationCodeRepositoryPort;
    private final UserMapper userMapper;
    private final PasswordHasher passwordHasher;

    public CreateUserServiceImpl(UserRepositoryPort userRepository, ActivationCodeRepositoryPort activationCodeRepositoryPort, UserMapper userMapper, PasswordHasher passwordHasher) {
        this.userRepository = userRepository;
        this.activationCodeRepositoryPort = activationCodeRepositoryPort;
        this.userMapper = userMapper;
        this.passwordHasher = passwordHasher;
    }

    @Override
    @Transactional
    public UserDTO run(User user) {
        String correlationId = CorrelationId.get().orElse(null);
        log.info("UserServiceImpl.run - start - correlationId [{}] - email [{}]", correlationId, user.getEmail());
        validateUserDoesNotExist(correlationId, user.getEmail());

        String hashedPassword = passwordHasher.hash(user.getPassword());
        user.setPassword(hashedPassword);

        User userCreated = userRepository.save(user);
        createActivationCode(userCreated);
        log.info("UserServiceImpl.run - end - correlationId [{}] - email [{}]", correlationId, user.getEmail());
        return userMapper.toDto(userCreated);
    }

    private void validateUserDoesNotExist(String correlationId, String email) {
        log.debug("UserServiceImpl.validateUserDoesNotExists - start - correlationId [{}] - email [{}]", correlationId, email);
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isPresent()) {
            log.debug("UserServiceImpl.validateUserDoesNotExists - end - correlationId [{}] - email [{}] already used", correlationId, email);
            throw new BadRequestException(String.format("User with email [%s] already used", email));
        }
        log.debug("UserServiceImpl.validateUserDoesNotExists - end - correlationId [{}] - email [{}]", correlationId, email);
    }

    private void createActivationCode(User userCreated) {
        String correlationId = CorrelationId.get().orElse(null);
        log.debug("UserServiceImpl.createActivationCode - start - correlationId [{}] - userId [{}] - email [{}]",
                correlationId, userCreated.getId(), userCreated.getEmail());

        SecureRandom random = new SecureRandom();
        int code = 100000 + random.nextInt(900000);

        Instant expiresAt = Instant.now().plus(15, ChronoUnit.MINUTES);
        ActivationCode activationCode = new ActivationCode(
                null,
                userCreated.getId(),
                String.valueOf(code),
                expiresAt,
                null
        );

        activationCodeRepositoryPort.save(activationCode);

        log.debug("UserServiceImpl.createActivationCode - end - correlationId [{}] - userId [{}] - email [{}] code [{}]",
                correlationId, userCreated.getId(), userCreated.getEmail(), code);
    }
}