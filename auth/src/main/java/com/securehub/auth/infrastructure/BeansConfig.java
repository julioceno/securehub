package com.securehub.auth.infrastructure;

import com.securehub.auth.application.mapper.UserMapper;
import com.securehub.auth.application.port.out.PasswordHasher;
import com.securehub.auth.application.port.out.TokenProviderPort;
import com.securehub.auth.application.service.auth.AuthenticateUserService;
import com.securehub.auth.application.service.user.ActivateUserServiceImpl;
import com.securehub.auth.application.service.user.CreateUserServiceImpl;
import com.securehub.auth.application.service.user.UserServiceImpl;
import com.securehub.auth.application.usecases.auth.AuthenticateUserUseCase;
import com.securehub.auth.application.usecases.user.ActivateUserUseCase;
import com.securehub.auth.application.usecases.user.CreateUserUseCases;
import com.securehub.auth.application.usecases.user.UserUseCases;
import com.securehub.auth.domain.activationCode.ActivationCodeRepositoryPort;
import com.securehub.auth.domain.user.UserRepositoryPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeansConfig {

    @Bean
    public UserUseCases userUseCases(CreateUserUseCases createUserService, ActivateUserUseCase activateUserUseCase) {
        return new UserServiceImpl(createUserService, activateUserUseCase);
    }

    @Bean
    public CreateUserUseCases createUserService(UserRepositoryPort userRepository, ActivationCodeRepositoryPort activationCodeRepository, UserMapper userMapper, PasswordHasher passwordHasher) {
        return new CreateUserServiceImpl(userRepository, activationCodeRepository, userMapper, passwordHasher);
    }

    @Bean
    public AuthenticateUserUseCase authenticateUserUseCase(
            UserRepositoryPort userRepository,
            PasswordHasher passwordHasher,
            TokenProviderPort tokenProviderPort
    ) {
        return new AuthenticateUserService(userRepository, passwordHasher, tokenProviderPort);
    }

    @Bean
    public ActivateUserUseCase activateUserUseCase(UserRepositoryPort userRepositoryPort, ActivationCodeRepositoryPort activationCodeRepositoryPort, UserMapper userMapper) {
        return new ActivateUserServiceImpl(userRepositoryPort, activationCodeRepositoryPort, userMapper);
    }
}
