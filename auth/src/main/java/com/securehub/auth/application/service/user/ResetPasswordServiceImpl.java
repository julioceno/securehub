package com.securehub.auth.application.service.user;

import com.securehub.auth.application.exception.BadRequestException;
import com.securehub.auth.application.port.out.PasswordHasher;
import com.securehub.auth.application.port.out.TokenEncryptorPort;
import com.securehub.auth.application.usecases.user.ResetPasswordUseCase;
import com.securehub.auth.application.util.CorrelationId;
import com.securehub.auth.domain.passwordResetToken.PasswordResetToken;
import com.securehub.auth.domain.passwordResetToken.PasswordResetTokenRepositoryPort;
import com.securehub.auth.domain.passwordResetToken.RequestPasswordResetTokenDTO;
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
    private final TokenEncryptorPort  tokenEncryptorPort;

    public ResetPasswordServiceImpl(
            UserRepositoryPort userRepositoryPort,
            PasswordResetTokenRepositoryPort passwordResetTokenRepositoryPort,
            PasswordHasher passwordHasher,
            TokenEncryptorPort  tokenEncryptorPort
    ) {
        this.userRepositoryPort = userRepositoryPort;
        this.passwordResetTokenRepositoryPort = passwordResetTokenRepositoryPort;
        this.passwordHasher = passwordHasher;
        this.tokenEncryptorPort = tokenEncryptorPort;
    }

    @Override
    public void run(RequestPasswordResetTokenDTO dto) {
        String correlationId = CorrelationId.get();
        log.info("ResetPasswordServiceImpl.run - start - correlationId [{}] - email [{}]", correlationId, dto.email());

        User user = getUser(dto.email());
        if (user == null) {
            return;
        }

        PasswordResetToken passwordResetToken = getToken(user.getId());
        if (passwordResetToken == null) {
            return;
        }
        boolean isValid = tokenEncryptorPort.compare(dto.token(), passwordResetToken.getToken());
        if (!isValid) {
            log.warn("ResetPasswordServiceImpl.run - correlationId [{}] is invalid", correlationId);
            return;
        }

        String passwordHashed = passwordHasher.hash(dto.newPassword());
        user.setPassword(passwordHashed);
        userRepositoryPort.save(user);

        passwordResetToken.setConfirmedAt(Instant.now());
        passwordResetTokenRepositoryPort.save(passwordResetToken);

        log.info("ResetPasswordServiceImpl.run - end - correlationId [{}] - email [{}]", correlationId, dto.email());
    }

    private User getUser(String email) {
        String correlationId = CorrelationId.get();
        log.debug("ResetPasswordServiceImpl.getUser - start - correlationId [{}] - email [{}]", correlationId, email);

        User user = userRepositoryPort.findByEmail(email).orElse(null);
        if (user == null) {
            log.warn("ResetPasswordServiceImpl.getUser - correlationId [{}] -user [{}] not found", correlationId, email);
            return null;
        }

        if (!Boolean.TRUE.equals(user.getEnabled())) {
            log.warn("ResetPasswordServiceImpl.getUser - invalid user - correlationId [{}]", correlationId);
            return null;
        }

        log.debug("ResetPasswordServiceImpl.getUser - end - correlationId [{}] - email [{}]", correlationId, email);
        return user;
    }

    private PasswordResetToken getToken(String userId) {
        String correlationId = CorrelationId.get();
        log.debug("ResetPasswordServiceImpl.getToken - start - correlationId [{}] - userId [{}]", correlationId, userId);

        PasswordResetToken passwordResetToken = passwordResetTokenRepositoryPort
                .findByUserIdAndConfirmedAtIsNullAndDeletedAtIsNull(userId)
                .orElse(null);

        if (passwordResetToken == null) {
            log.warn("ResetPasswordServiceImpl.getToken - token/user mismatch - correlationId [{}]", correlationId);
            return null;
        }

        if (passwordResetToken.getDeletedAt() != null) {
            log.warn("ResetPasswordServiceImpl.getToken - token is deleted - correlationId [{}]", correlationId);
            return null;
        }

        if (passwordResetToken.getConfirmedAt() != null) {
            log.warn("ResetPasswordServiceImpl.getToken - token already used - correlationId [{}]", correlationId);
            return null;
        }
        if (passwordResetToken.getExpiresAt().isBefore(Instant.now())) {
            log.warn("ResetPasswordServiceImpl.getToken - token expired - correlationId [{}]", correlationId);
            return null;
        }

        log.debug("ResetPasswordServiceImpl.getToken - end - correlationId [{}] - userId [{}]", correlationId, userId);
        return passwordResetToken;
    }
}