package com.securehub.mailsender.infrastructure.config;

import com.securehub.mailsender.application.port.out.EmailTemplatePort;
import com.securehub.mailsender.application.port.out.MailSenderPort;
import com.securehub.mailsender.application.service.SendMailUseCaseImpl;
import com.securehub.mailsender.application.usecases.SendMailUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeansConfig {

    @Bean
    public SendMailUseCase sendMailUseCase(MailSenderPort mailSender, EmailTemplatePort templatePort) {
        return new SendMailUseCaseImpl(mailSender, templatePort);
    }
}
