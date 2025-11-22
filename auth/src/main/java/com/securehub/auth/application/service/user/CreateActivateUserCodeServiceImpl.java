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
import com.securehub.auth.domain.user.User;
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
    public void run(User user, String baseUrl) {
        String correlationId = CorrelationId.get();
        log.debug("CreateActivateUserCodeServiceImpl.run - start - correlationId [{}] - userId [{}]",
                correlationId, user.getId());

        invalidateOldCodeIfExists(user.getId());

        String rawCode = GenerateCode.generateCode();
        String url = generateUrl(baseUrl, user.getId(), rawCode);
        String code = generateEncryptedCode(rawCode);
        Instant expiresAt = Instant.now().plus(15, ChronoUnit.MINUTES);

        ActivationCode activationCode = new ActivationCode(
                null,
                user.getId(),
                code,
                expiresAt,
                null,
                null
        );

        activationCodeRepositoryPort.save(activationCode);

        sendMail(user, url);
        log.debug("CreateActivateUserCodeServiceImpl.run - end - correlationId [{}] - userId [{}] ",
                correlationId, user.getId());
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

    private String generateEncryptedCode(String rawCode) {
        try {
            return signerPort.encrypt(rawCode);
        } catch (Exception e) {
            throw new BadRequestException("An error occurred while generating the code");
        }
    };

    private String generateUrl(String baseUrl, String userId, String rawCode) {
        String correlationId = CorrelationId.get();
        log.debug("CreateActivateUserCodeServiceImpl.generateUrl - start - correlationId [{}] - baseUrl [{}]", correlationId, baseUrl);
        String url = String.format("%s/users/%s/enable/%s", baseUrl, userId, rawCode);
        log.debug("CreateActivateUserCodeServiceImpl.generateUrl - end - correlationId [{}] - baseUrl [{}]", correlationId, baseUrl);
        return url;
    }

    private void sendMail(User user, String url) {
        String correlationId = CorrelationId.get();
        log.debug("CreateActivateUserCodeServiceImpl.sendMail - start - correlationId [{}]",  correlationId);
        EmailMessage emailMessage = new EmailMessage(
                user.getEmail(),
                "template", // TODO: change
                Map.of(
                        "username", user.getUsername(),
                        "url", url
                )
        );
        eventPublisherPort.send(emailMessage);
        log.debug("CreateActivateUserCodeServiceImpl.sendMail - end - correlationId [{}]",  correlationId);
    }
}
