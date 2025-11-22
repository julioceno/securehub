package com.securehub.mailsender.application.service;

import com.securehub.mailsender.application.port.out.EmailTemplatePort;
import com.securehub.mailsender.application.port.out.MailSenderPort;
import com.securehub.mailsender.application.usecases.SendMailUseCase;
import com.securehub.mailsender.domain.EmailMessage;

// TODO: add logs
public class SendMailUseCaseImpl implements SendMailUseCase {
    private final MailSenderPort mailSender;
    private final EmailTemplatePort templatePort;

    public SendMailUseCaseImpl(MailSenderPort mailSender, EmailTemplatePort templatePort) {
        this.mailSender = mailSender;
        this.templatePort = templatePort;
    }

    @Override
    public void run(EmailMessage emailMessage) {
        String body = templatePort.processTemplate(emailMessage.getTemplate(), emailMessage.getParams());
        mailSender.send(emailMessage.getTo(), "subject elaborado", body);
    }
}
