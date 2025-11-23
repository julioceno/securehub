package com.securehub.auth.infrastructure.config;

import com.securehub.auth.application.mapper.UserMapper;
import com.securehub.auth.application.port.out.EmailSenderPort;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class BeansConfigTest {

    @InjectMocks
    private BeansConfig beansConfig;

    @Mock
    private UserRepositoryPort userRepository;
    @Mock
    private ActivationCodeRepositoryPort activationCodeRepository;
    @Mock
    private PasswordResetTokenRepositoryPort passwordResetTokenRepository;
    @Mock
    private UserMapper userMapper;
    @Mock
    private PasswordHasher passwordHasher;
    @Mock
    private SignerPort signerPort;
    @Mock
    private EmailSenderPort emailSenderPort;
    @Mock
    private TokenProviderPort tokenProviderPort;

    @Test
    void shouldCreateUserUseCasesBean() {
        CreateUserUseCases createUserService = mock(CreateUserUseCases.class);
        ActivateUserUseCase activateUserUseCase = mock(ActivateUserUseCase.class);
        ForgotPasswordUseCase forgotPasswordUseCase = mock(ForgotPasswordUseCase.class);
        ResetPasswordUseCase resetPasswordUseCase = mock(ResetPasswordUseCase.class);

        UserUseCases result = beansConfig.userUseCases(
                createUserService,
                activateUserUseCase,
                forgotPasswordUseCase,
                resetPasswordUseCase
        );

        assertNotNull(result);
        assertInstanceOf(UserServiceImpl.class, result);
    }

    @Test
    void shouldCreateCreateUserServiceBean() {
        CreateActivateUserCodeUseCase createActivateUserCodeUseCase = mock(CreateActivateUserCodeUseCase.class);

        CreateUserUseCases result = beansConfig.createUserService(
                userRepository,
                userMapper,
                passwordHasher,
                createActivateUserCodeUseCase
        );

        assertNotNull(result);
        assertInstanceOf(CreateUserServiceImpl.class, result);
    }

    @Test
    void shouldCreateActivateUserUseCaseBean() {
        ActivateUserUseCase result = beansConfig.activateUserUseCase(
                userRepository,
                activationCodeRepository,
                userMapper,
                signerPort
        );

        assertNotNull(result);
        assertInstanceOf(ActivateUserServiceImpl.class, result);
    }

    @Test
    void shouldCreateCreateActivateUserCodeUseCaseBean() {
        CreateActivateUserCodeUseCase result = beansConfig.createPasswordResetTokenUseCase(
                activationCodeRepository,
                signerPort,
                emailSenderPort
        );

        assertNotNull(result);
        assertInstanceOf(CreateActivateUserCodeServiceImpl.class, result);
    }

    @Test
    void shouldCreateForgotPasswordUseCaseBean() {
        ForgotPasswordUseCase result = beansConfig.forgotPasswordUseCase(
                userRepository,
                passwordResetTokenRepository,
                signerPort,
                emailSenderPort
        );

        assertNotNull(result);
        assertInstanceOf(ForgotPasswordServiceImpl.class, result);
    }

    @Test
    void shouldCreateResetPasswordUseCaseBean() {
        ResetPasswordUseCase result = beansConfig.resetPasswordUseCase(
                userRepository,
                passwordResetTokenRepository,
                passwordHasher,
                signerPort
        );

        assertNotNull(result);
        assertInstanceOf(ResetPasswordServiceImpl.class, result);
    }

    @Test
    void shouldCreateAuthenticateUserUseCaseBean() {
        CreateActivateUserCodeUseCase createActivateUserCodeUseCase = mock(CreateActivateUserCodeUseCase.class);

        AuthenticateUserUseCase result = beansConfig.authenticateUserUseCase(
                userRepository,
                passwordHasher,
                tokenProviderPort,
                activationCodeRepository,
                createActivateUserCodeUseCase
        );

        assertNotNull(result);
        assertInstanceOf(AuthenticateUserService.class, result);
    }
}