package com.securehub.auth.infrastructure;

import com.securehub.auth.application.mapper.UserMapper;
import com.securehub.auth.application.port.out.PasswordHasher;
import com.securehub.auth.application.service.user.CreateUserServiceImpl;
import com.securehub.auth.application.service.user.UserServiceImpl;
import com.securehub.auth.application.usecases.user.UserUseCases;
import com.securehub.auth.domain.user.UserRepositoryPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeansConfig {

    @Bean
    public UserUseCases userUseCases(CreateUserServiceImpl createUserService) {
        return new UserServiceImpl(createUserService);
    }

    @Bean
    public CreateUserServiceImpl createUserService(UserRepositoryPort userRepository, UserMapper userMapper, PasswordHasher passwordHasher) {
        return new CreateUserServiceImpl(userRepository, userMapper, passwordHasher);
    }
}
