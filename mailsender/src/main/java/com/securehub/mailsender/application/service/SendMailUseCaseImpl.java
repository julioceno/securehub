package com.securehub.mailsender.application.service;

import com.securehub.mailsender.application.port.out.MailSenderPort;
import com.securehub.mailsender.application.usecases.SendMailUseCase;
import com.securehub.mailsender.domain.EmailMessage;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

@Service
public class SendMailUseCaseImpl implements SendMailUseCase {
    private final MailSenderPort mailSender;

    public SendMailUseCaseImpl(MailSenderPort mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void run(EmailMessage emailMessage) {
        String template = loadTemplate();

        mailSender.send(emailMessage.getTo(), "subject elaborado", template);
    }

    // TODO: this implementation isn't thread safe
    public String loadTemplate() {
        try {
            ClassPathResource resource = new ClassPathResource("templates/account-activation.html");
            return new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
