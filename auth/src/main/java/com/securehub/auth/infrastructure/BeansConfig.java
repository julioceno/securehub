package com.securehub.auth.infrastructure;

import com.securehub.auth.application.mapper.UserMapper;
import com.securehub.auth.application.port.out.PasswordHasher;
import com.securehub.auth.application.port.out.SignerPort;
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
    public CreateUserUseCases createUserService(
            UserRepositoryPort userRepository,
            UserMapper userMapper,
            PasswordHasher passwordHasher,
            CreateActivateUserCodeUseCase createActivateUserCodeUseCase
    ) {
        return new CreateUserServiceImpl(userRepository, userMapper, passwordHasher, createActivateUserCodeUseCase);
    }

    @Bean
    public ActivateUserUseCase activateUserUseCase(
            UserRepositoryPort userRepositoryPort,
            ActivationCodeRepositoryPort activationCodeRepositoryPort,
            UserMapper userMapper,
            SignerPort signerPort
    ) {
        return new ActivateUserServiceImpl(userRepositoryPort, activationCodeRepositoryPort, userMapper, signerPort);
    }

    @Bean
    public CreateActivateUserCodeUseCase createPasswordResetTokenUseCase(ActivationCodeRepositoryPort activationCodeRepositoryPort, SignerPort signerPort) {
        return new CreateActivateUserCodeServiceImpl(activationCodeRepositoryPort, signerPort);
    }

    @Bean
    public ForgotPasswordUseCase forgotPasswordUseCase(
            UserRepositoryPort userRepositoryPort,
            PasswordResetTokenRepositoryPort passwordResetTokenRepositoryPort,
            SignerPort signerPort
    ) {
        return new ForgotPasswordServiceImpl(userRepositoryPort, passwordResetTokenRepositoryPort, signerPort);
    }

    @Bean
    public ResetPasswordUseCase resetPasswordUseCase(
            UserRepositoryPort userRepositoryPort,
            PasswordResetTokenRepositoryPort passwordResetTokenRepositoryPort,
            PasswordHasher passwordHasher,
            SignerPort signerPort
    ) {
        return new ResetPasswordServiceImpl(userRepositoryPort, passwordResetTokenRepositoryPort, passwordHasher, signerPort);
    }

    @Bean
    public AuthenticateUserUseCase authenticateUserUseCase(
            UserRepositoryPort userRepository,
            PasswordHasher passwordHasher,
            TokenProviderPort tokenProviderPort,
            ActivationCodeRepositoryPort activationCodeRepositoryPort,
            CreateActivateUserCodeUseCase createActivateUserCodeUseCase
    ) {
        return new AuthenticateUserService(userRepository, passwordHasher, tokenProviderPort, activationCodeRepositoryPort, createActivateUserCodeUseCase);
    }
}
