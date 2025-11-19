package com.securehub.auth.application.service.user;

import com.securehub.auth.application.usecases.user.ForgotPasswordUseCase;
import com.securehub.auth.application.util.CorrelationId;
import com.securehub.auth.domain.passwordResetToken.PasswordResetToken;
import com.securehub.auth.domain.passwordResetToken.PasswordResetTokenRepositoryPort;
import com.securehub.auth.domain.user.User;
import com.securehub.auth.domain.user.UserRepositoryPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

public class ForgotPasswordServiceImpl implements ForgotPasswordUseCase {
    private static final Logger log = LoggerFactory.getLogger(ForgotPasswordServiceImpl.class);

    private final UserRepositoryPort userRepositoryPort;
    private final PasswordResetTokenRepositoryPort passwordResetTokenRepositoryPort;

    public ForgotPasswordServiceImpl(UserRepositoryPort userRepositoryPort, PasswordResetTokenRepositoryPort passwordResetTokenRepositoryPort) {
        this.userRepositoryPort = userRepositoryPort;
        this.passwordResetTokenRepositoryPort = passwordResetTokenRepositoryPort;
    }

    @Override
    public void run(String email) {
        String correlationId = CorrelationId.get();
        log.info("ForgotPasswordServiceImpl.createToken - start - correlationId [{}] - email [{}]", correlationId, email);

        User user = userRepositoryPort.findByEmail(email).orElse(null);
        if (user == null || !user.getEnabled()) return;

        SecureRandom random = new SecureRandom();
        int token = 100000 + random.nextInt(900000);

        Instant expiresAt = Instant.now().plus(15, ChronoUnit.MINUTES);
        PasswordResetToken activationCode = new PasswordResetToken(
                null,
                user.getId(),
                String.valueOf(token),
                expiresAt,
                null
        );

        passwordResetTokenRepositoryPort.save(activationCode);

        log.info("ForgotPasswordServiceImpl.createToken - end - correlationId [{}] - userId [{}] - email [{}] token [{}]",
                correlationId, user.getId(), user.getEmail(), token);
    }
}
