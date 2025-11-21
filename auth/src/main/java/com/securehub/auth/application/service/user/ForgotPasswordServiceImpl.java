package com.securehub.auth.application.service.user;

import com.securehub.auth.application.exception.BadRequestException;
import com.securehub.auth.application.port.out.SignerPort;
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
    private final SignerPort signerPort;

    public ForgotPasswordServiceImpl(
            UserRepositoryPort userRepositoryPort,
            PasswordResetTokenRepositoryPort passwordResetTokenRepositoryPort,
            SignerPort signerPort
    ) {
        this.userRepositoryPort = userRepositoryPort;
        this.passwordResetTokenRepositoryPort = passwordResetTokenRepositoryPort;
        this.signerPort = signerPort;
    }

    @Override
    public void run(String email) {
        String correlationId = CorrelationId.get();
        log.info("ForgotPasswordServiceImpl.run - start - correlationId [{}] - email [{}]", correlationId, email);

        User user = userRepositoryPort.findByEmail(email).orElse(null);
        if (user == null || !user.getEnabled()) return;

        invalidateOldTokenIfExists(user.getId());
        String token = generateToken();
        Instant expiresAt = Instant.now().plus(15, ChronoUnit.MINUTES);
        PasswordResetToken passwordResetToken = new PasswordResetToken(
                null,
                user.getId(),
                String.valueOf(token),
                expiresAt,
                null,
                null
        );

        passwordResetTokenRepositoryPort.save(passwordResetToken);

        log.info("ForgotPasswordServiceImpl.run - end - correlationId [{}] - userId [{}] - email [{}] token [{}]",
                correlationId, user.getId(), user.getEmail(), token);
    }

    private void invalidateOldTokenIfExists(String userId) {
        String correlationId = CorrelationId.get();
        log.debug("ForgotPasswordServiceImpl.invalidateOldTokenIfExists - start - correlationId [{}]", correlationId);

        passwordResetTokenRepositoryPort.findByUserIdAndConfirmedAtIsNullAndDeletedAtIsNull(userId).ifPresent(passwordResetTokenEntity -> {
            log.debug("ForgotPasswordServiceImpl.invalidateOldTokenIfExists - set deleted date in password reset token - correlationId [{}] - passwordResetTokenId [{}] ", correlationId, passwordResetTokenEntity.getId());
            passwordResetTokenEntity.setDeletedAt(Instant.now());
            passwordResetTokenRepositoryPort.save(passwordResetTokenEntity);
        });

        log.debug("ForgotPasswordServiceImpl.invalidateOldTokenIfExists - end - correlationId [{}]", correlationId);

    }

    private String generateToken() {
        String correlationId = CorrelationId.get();
        log.debug("ForgotPasswordServiceImpl.generateToken - start - correlationId [{}]", correlationId);
        try {
            SecureRandom random = new SecureRandom();
            int token = 100000 + random.nextInt(900000);

            String encryptedToken = signerPort.encrypt(String.valueOf(token));
            log.debug("ForgotPasswordServiceImpl.generateToken - end - correlationId [{}]", correlationId);
            return encryptedToken;
        } catch(Exception ex) {
            log.error("ForgotPasswordServiceImpl.generateToken - an error occurred while generating the token - correlationId [{}]", correlationId);
            throw new BadRequestException("An error occurred while generating the token");
        }
    }
}
