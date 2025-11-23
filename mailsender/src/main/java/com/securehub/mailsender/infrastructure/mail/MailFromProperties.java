package com.securehub.mailsender.infrastructure.mail;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "mail.from")
public class MailFromProperties {
    private String email;
    private String name;
}