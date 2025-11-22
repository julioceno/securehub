package com.securehub.mailsender.application.service;

import com.securehub.mailsender.application.port.out.MailSenderPort;
import com.securehub.mailsender.application.usecases.SendMailUseCase;
import com.securehub.mailsender.domain.EmailMessage;
import org.springframework.stereotype.Service;

@Service
public class SendMailUseCaseImpl implements SendMailUseCase {
    private final MailSenderPort mailSender;

    public SendMailUseCaseImpl(MailSenderPort mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void run(EmailMessage emailMessage) {
        mailSender.send(emailMessage.getTo(), "subject elaborado", "body");
    }
}
