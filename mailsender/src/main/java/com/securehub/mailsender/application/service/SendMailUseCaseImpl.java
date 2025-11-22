package com.securehub.mailsender.application.service;

import com.securehub.mailsender.application.port.out.MailSenderPort;
import com.securehub.mailsender.application.usecases.SendMailUseCase;
import com.securehub.mailsender.domain.EmailMessage;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Map;

@Service
public class SendMailUseCaseImpl implements SendMailUseCase {
    private final MailSenderPort mailSender;

    public SendMailUseCaseImpl(MailSenderPort mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void run(EmailMessage emailMessage) {
        String template = loadTemplate(emailMessage.getTemplate());
        String processedTemplate = processTemplate(template, emailMessage.getParams());

        processTemplate(template, emailMessage.getParams());
        mailSender.send(emailMessage.getTo(), "subject elaborado", processedTemplate);
    }

    public String loadTemplate(String templateName) {
        try {
            String templatePath = "templates/" + templateName + ".html";
            ClassPathResource resource = new ClassPathResource(templatePath);
            return new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String processTemplate(String template, Map<String, Object> params) {
        String result = template;
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            String key = "{{" + entry.getKey() + "}}";
            String value = entry.getValue() != null ? entry.getValue().toString() : "";
            result = result.replace(key, value);
        }
        return result;
    }

}
