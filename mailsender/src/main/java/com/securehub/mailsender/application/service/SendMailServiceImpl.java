package com.securehub.mailsender.application.service;

import com.securehub.mailsender.application.port.out.EmailTemplatePort;
import com.securehub.mailsender.application.port.out.MailSenderPort;
import com.securehub.mailsender.application.usecases.SendMailUseCase;
import com.securehub.mailsender.application.util.CorrelationId;
import com.securehub.mailsender.domain.EmailMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SendMailServiceImpl implements SendMailUseCase {
    private static final Logger log = LoggerFactory.getLogger(SendMailServiceImpl.class);

    private final MailSenderPort mailSender;
    private final EmailTemplatePort templatePort;

    public SendMailServiceImpl(MailSenderPort mailSender, EmailTemplatePort templatePort) {
        this.mailSender = mailSender;
        this.templatePort = templatePort;
    }

    @Override
    public void run(EmailMessage emailMessage) {
        String correlationId = CorrelationId.get();
        log.info("SendMailServiceImpl.run - start - correlationId [{}]", correlationId);
        String body = templatePort.processTemplate(emailMessage.getTemplate(), emailMessage.getParams());

        mailSender.send(emailMessage.getTo(), emailMessage.getSubject(), body);
        log.info("SendMailServiceImpl.run - end - correlationId [{}]", correlationId);
    }
}
