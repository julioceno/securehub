package com.securehub.mailsender.infrastructure.mail;

import com.securehub.mailsender.application.port.out.MailSenderPort;
import jakarta.mail.internet.MimeMessage;
import lombok.AllArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

// TODO: change name
@Component
@AllArgsConstructor
public class JavaMailSenderAdapter implements MailSenderPort {
    private final JavaMailSender mailSender;

    @Override
    public void send(String to, String subject, String body) {
        try {
            MimeMessage msg = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(msg, true);

            // TODO: change from
            helper.setFrom("noreply@gmail.com");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body, true);
            mailSender.send(msg);
        } catch (Exception e) {

            throw new RuntimeException(e);
        }

    }
}
