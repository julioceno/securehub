package com.securehub.mailsender.infrastructure.mail;

import com.securehub.mailsender.application.port.out.MailSenderPort;
import com.securehub.mailsender.application.util.CorrelationId;
import jakarta.mail.internet.MimeMessage;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class JavaMailSenderAdapter implements MailSenderPort {
    private static final Logger log = LoggerFactory.getLogger(JavaMailSenderAdapter.class);

    private final JavaMailSender mailSender;

    @Override
    public void send(String to, String subject, String body) {
        try {
            String correlationId = CorrelationId.get();
            log.debug("JavaMailSenderAdapter.run - start - correlationId [{}] - email to [{}]", correlationId, to);

            MimeMessage msg = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(msg, true);

            helper.setFrom("noreply@gmail.com");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body, true);

            log.debug("JavaMailSenderAdapter.run - message created - correlationId [{}]  ", correlationId);

            mailSender.send(msg);
            log.debug("JavaMailSenderAdapter.run - end - correlationId [{}] - email to [{}]", correlationId, to);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
}
