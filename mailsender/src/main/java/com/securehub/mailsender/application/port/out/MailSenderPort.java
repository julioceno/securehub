package com.securehub.mailsender.application.port.out;

public interface MailSenderPort {
    void send(String to, String subject, String body);
}
