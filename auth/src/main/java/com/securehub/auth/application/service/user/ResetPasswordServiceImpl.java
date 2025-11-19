package com.securehub.auth.application.service.user;

import com.securehub.auth.application.port.out.PasswordHasher;
import com.securehub.auth.application.usecases.user.ResetPasswordUseCase;
import com.securehub.auth.application.util.CorrelationId;
import com.securehub.auth.domain.passwordResetToken.PasswordResetToken;
import com.securehub.auth.domain.passwordResetToken.PasswordResetTokenRepositoryPort;
import com.securehub.auth.domain.user.User;
import com.securehub.auth.domain.user.UserRepositoryPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;

public class ResetPasswordServiceImpl implements ResetPasswordUseCase {
    private static final Logger log = LoggerFactory.getLogger(ResetPasswordServiceImpl.class);

    private final UserRepositoryPort userRepositoryPort;
    private final PasswordResetTokenRepositoryPort passwordResetTokenRepositoryPort;
    private final PasswordHasher passwordHasher;

    public ResetPasswordServiceImpl(
            UserRepositoryPort userRepositoryPort,
            PasswordResetTokenRepositoryPort passwordResetTokenRepositoryPort,
            PasswordHasher passwordHasher
    ) {
        this.userRepositoryPort = userRepositoryPort;
        this.passwordResetTokenRepositoryPort = passwordResetTokenRepositoryPort;
        this.passwordHasher = passwordHasher;
    }

    @Override
    public void run(String userId, String token, String newPassword) {
        String correlationId = CorrelationId.get();
        log.info("ResetPasswordServiceImpl.run - start - correlationId [{}] - userId [{}]", correlationId, userId);

        PasswordResetToken passwordResetToken = passwordResetTokenRepositoryPort
                .findByUserIdAndTokenAndConfirmedAtIsNull(userId, token)
                .orElse(null);

        if (passwordResetToken == null) {
            log.warn("ResetPasswordServiceImpl.run - token/user mismatch - correlationId [{}]", correlationId);
            return;
        }
        if (passwordResetToken.getConfirmedAt() != null) {
            log.warn("ResetPasswordServiceImpl.run - token already used - correlationId [{}]", correlationId);
            return;
        }
        if (passwordResetToken.getExpiresAt().isBefore(Instant.now())) {
            log.warn("ResetPasswordServiceImpl.run - token expired - correlationId [{}]", correlationId);
            return;
        }

        User user = userRepositoryPort.findByEmail(userId).orElse(null);
        if (user == null || !Boolean.TRUE.equals(user.getEnabled())) {
            log.warn("ResetPasswordServiceImpl.run - invalid user - correlationId [{}]", correlationId);
            return;
        }

        String passwordHashed = passwordHasher.hash(newPassword);
        user.setPassword(passwordHashed);
        userRepositoryPort.save(user);

        passwordResetToken.setConfirmedAt(Instant.now());
        passwordResetTokenRepositoryPort.save(passwordResetToken);

        log.info("ResetPasswordServiceImpl.run - end - correlationId [{}] - userId [{}]", correlationId, userId);
    }
}