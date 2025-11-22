package com.securehub.mailsender.infrastructure.mail;

import com.securehub.mailsender.application.port.out.MailSenderPort;
import lombok.AllArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

// TODO: change name
@Component
@AllArgsConstructor
public class JavaMailSenderAdapter implements MailSenderPort {
    private final JavaMailSender mailSender;

    @Override
    public void send(String to, String subject, String body) {
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(to);
        msg.setText(body);
        msg.setSubject(subject);
        mailSender.send(msg);
    }
}
