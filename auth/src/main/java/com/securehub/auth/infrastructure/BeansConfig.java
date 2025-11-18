package com.securehub.auth.infrastructure;

import com.securehub.auth.application.mapper.UserMapper;
import com.securehub.auth.application.port.out.PasswordHasher;
import com.securehub.auth.application.port.out.TokenProviderPort;
import com.securehub.auth.application.service.auth.AuthenticateUserService;
import com.securehub.auth.application.service.user.CreateUserServiceImpl;
import com.securehub.auth.application.service.user.UserServiceImpl;
import com.securehub.auth.application.usecases.auth.AuthenticateUserUseCase;
import com.securehub.auth.application.usecases.user.CreateUserUseCases;
import com.securehub.auth.application.usecases.user.UserUseCases;
import com.securehub.auth.domain.user.UserRepositoryPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeansConfig {

    @Bean
    public UserUseCases userUseCases(CreateUserUseCases createUserService) {
        return new UserServiceImpl(createUserService);
    }

    @Bean
    public CreateUserUseCases createUserService(UserRepositoryPort userRepository, UserMapper userMapper, PasswordHasher passwordHasher) {
        return new CreateUserServiceImpl(userRepository, userMapper, passwordHasher);
    }

    @Bean
    public AuthenticateUserUseCase authenticateUserUseCase(
            UserRepositoryPort userRepository,
            PasswordHasher passwordHasher,
            TokenProviderPort tokenProviderPort
    ) {
        return new AuthenticateUserService(userRepository, passwordHasher, tokenProviderPort);
    }
}
