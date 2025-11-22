package com.securehub.auth.application.service.user;

import com.securehub.auth.application.exception.BadRequestException;
import com.securehub.auth.application.port.out.EmailSenderPort;
import com.securehub.auth.application.port.out.SignerPort;
import com.securehub.auth.application.usecases.user.CreateActivateUserCodeUseCase;
import com.securehub.auth.application.util.CorrelationId;
import com.securehub.auth.application.util.GenerateCode;
import com.securehub.auth.domain.activationCode.ActivationCode;
import com.securehub.auth.domain.activationCode.ActivationCodeRepositoryPort;
import com.securehub.auth.domain.email.EmailMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;

public class CreateActivateUserCodeServiceImpl implements CreateActivateUserCodeUseCase {
    private static final Logger log = LoggerFactory.getLogger(CreateActivateUserCodeServiceImpl.class);

    private final ActivationCodeRepositoryPort activationCodeRepositoryPort;
    private final SignerPort signerPort;
    private final EmailSenderPort eventPublisherPort;

    public CreateActivateUserCodeServiceImpl(
            ActivationCodeRepositoryPort activationCodeRepositoryPort,
            SignerPort signerPort,
            EmailSenderPort eventPublisherPort
    ) {
        this.activationCodeRepositoryPort = activationCodeRepositoryPort;
        this.signerPort = signerPort;
        this.eventPublisherPort = eventPublisherPort;

    }

    @Override
    public void run(String userId) {
        String correlationId = CorrelationId.get();
        log.debug("CreateActivateUserCodeServiceImpl.run - start - correlationId [{}] - userId [{}]",
                correlationId, userId);

        invalidateOldCodeIfExists(userId);

        String code = generateEncryptedCode();
        Instant expiresAt = Instant.now().plus(15, ChronoUnit.MINUTES);

        ActivationCode activationCode = new ActivationCode(
                null,
                userId,
                code,
                expiresAt,
                null,
                null
        );

        activationCodeRepositoryPort.save(activationCode);

        log.debug("CreateActivateUserCodeServiceImpl.run - end - correlationId [{}] - userId [{}] ",
                correlationId, userId);
    }

    private void invalidateOldCodeIfExists(String userId) {
        String correlationId = CorrelationId.get();
        log.debug("CreateActivateUserCodeServiceImpl.invalidateOldCodeIfExists - start - correlationId [{}]", correlationId);

        activationCodeRepositoryPort.findByUserIdAndConfirmedAtIsNullAndDeletedAtIsNull(userId).ifPresent(activationCode -> {
            log.debug("CreateActivateUserCodeServiceImpl.invalidateOldCodeIfExists - set deleted date in password reset token - correlationId [{}] - activationCodeId [{}] ", correlationId, activationCode.getId());

            activationCode.setDeletedAt(Instant.now());
            activationCodeRepositoryPort.save(activationCode);
        });

        log.debug("CreateActivateUserCodeServiceImpl.invalidateOldCodeIfExists - end - correlationId [{}]", correlationId);
    }

    private String generateEncryptedCode() {
        try {
            String code = GenerateCode.generateCode();

            EmailMessage emailMessage = new EmailMessage(
                    "email@gmail.com",
                    "template",
                    Map.of(
                            "username", "username",
                            "code", code
                    )
            );
            eventPublisherPort.send(emailMessage);
            return signerPort.encrypt(code);
        } catch (Exception e) {
            throw new BadRequestException("An error occurred while generating the code");
        }
    };
}
