package com.securehub.auth.application.service.user;

import com.securehub.auth.application.exception.BadRequestException;
import com.securehub.auth.application.mapper.UserMapper;
import com.securehub.auth.application.port.out.PasswordHasher;
import com.securehub.auth.application.usecases.user.CreateActivateUserCodeUseCase;
import com.securehub.auth.application.usecases.user.CreateUserUseCases;
import com.securehub.auth.application.util.CorrelationId;
import com.securehub.auth.domain.user.User;
import com.securehub.auth.domain.user.UserDTO;
import com.securehub.auth.domain.user.UserRepositoryPort;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class CreateUserServiceImpl implements CreateUserUseCases {
    private static final Logger log = LoggerFactory.getLogger(CreateUserServiceImpl.class);

    private final UserRepositoryPort userRepository;
    private final UserMapper userMapper;
    private final PasswordHasher passwordHasher;
    private final CreateActivateUserCodeUseCase createActivateUserCodeUseCase;

    public CreateUserServiceImpl(UserRepositoryPort userRepository, UserMapper userMapper, PasswordHasher passwordHasher, CreateActivateUserCodeUseCase createActivateUserCodeUseCase) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.passwordHasher = passwordHasher;
        this.createActivateUserCodeUseCase = createActivateUserCodeUseCase;
    }

    @Override
    @Transactional
    public UserDTO run(User user) {
        String correlationId = CorrelationId.get();
        log.info("UserServiceImpl.run - start - correlationId [{}] - email [{}]", correlationId, user.getEmail());
        validateUserDoesNotExist(correlationId, user.getEmail());

        String hashedPassword = passwordHasher.hash(user.getPassword());
        user.setPassword(hashedPassword);

        User userCreated = userRepository.save(user);
        createActivateUserCodeUseCase.run(userCreated);

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
}