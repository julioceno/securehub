package com.securehub.auth.infrastructure;

import com.securehub.auth.application.mapper.UserMapper;
import com.securehub.auth.application.port.out.PasswordHasher;
import com.securehub.auth.application.port.out.TokenProviderPort;
import com.securehub.auth.application.service.auth.AuthenticateUserService;
import com.securehub.auth.application.service.user.*;
import com.securehub.auth.application.usecases.auth.AuthenticateUserUseCase;
import com.securehub.auth.application.usecases.user.*;
import com.securehub.auth.domain.activationCode.ActivationCodeRepositoryPort;
import com.securehub.auth.domain.passwordResetToken.PasswordResetTokenRepositoryPort;
import com.securehub.auth.domain.user.UserRepositoryPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeansConfig {

    @Bean
    public UserUseCases userUseCases(
            CreateUserUseCases createUserService,
            ActivateUserUseCase activateUserUseCase,
            ForgotPasswordUseCase forgotPasswordUseCase,
            ResetPasswordUseCase resetPasswordUseCase
        ) {
        return new UserServiceImpl(createUserService, activateUserUseCase, forgotPasswordUseCase, resetPasswordUseCase);
    }

    @Bean
    public CreateUserUseCases createUserService(UserRepositoryPort userRepository, ActivationCodeRepositoryPort activationCodeRepository, UserMapper userMapper, PasswordHasher passwordHasher) {
        return new CreateUserServiceImpl(userRepository, activationCodeRepository, userMapper, passwordHasher);
    }

    @Bean
    public ActivateUserUseCase activateUserUseCase(UserRepositoryPort userRepositoryPort, ActivationCodeRepositoryPort activationCodeRepositoryPort, UserMapper userMapper) {
        return new ActivateUserServiceImpl(userRepositoryPort, activationCodeRepositoryPort, userMapper);
    }

    @Bean
    public ForgotPasswordUseCase  forgotPasswordUseCase(UserRepositoryPort userRepositoryPort, PasswordResetTokenRepositoryPort passwordResetTokenRepositoryPort) {
        return new ForgotPasswordServiceImpl(userRepositoryPort, passwordResetTokenRepositoryPort);
    }

    @Bean
    public ResetPasswordUseCase resetPasswordUseCase(UserRepositoryPort userRepositoryPort, PasswordResetTokenRepositoryPort passwordResetTokenRepositoryPort, PasswordHasher passwordHasher) {
        return new ResetPasswordServiceImpl(userRepositoryPort, passwordResetTokenRepositoryPort, passwordHasher);
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
