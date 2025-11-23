package com.securehub.mailsender.application.service;

import com.securehub.mailsender.application.port.out.EmailTemplatePort;
import com.securehub.mailsender.application.port.out.MailSenderPort;
import com.securehub.mailsender.domain.EmailMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SendMailServiceImplTest {

    @Mock
    private MailSenderPort mailSender;

    @Mock
    private EmailTemplatePort templatePort;

    @InjectMocks
    private SendMailServiceImpl sendMailServiceImpl;

    @Test
    void shouldCallTemplatePortAndMailSender() {
        EmailMessage emailMessage = new EmailMessage(
            "to@securehub.com",
            "subject",
            "template",
            null
        );

        String html = "<div></div>";
        when(templatePort.processTemplate(emailMessage.getTemplate(), emailMessage.getParams())).thenReturn(html);
        doNothing().when(mailSender).send(emailMessage.getTo(), emailMessage.getSubject(), html);

        sendMailServiceImpl.run(emailMessage);

        verify(templatePort).processTemplate(emailMessage.getTemplate(), emailMessage.getParams());
        verify(mailSender).send(emailMessage.getTo(), emailMessage.getSubject(), html);
    }

    @Test
    void shouldHandleEmailMessageWithParams() {
        Map<String, Object> params = Map.of("name", "João", "token", "abc123");
        EmailMessage emailMessage = new EmailMessage(
                "user@example.com",
                "Bem-vindo",
                "welcome-template",
                params
        );

        String processedHtml = "<div>Olá João, seu token é abc123</div>";
        when(templatePort.processTemplate(emailMessage.getTemplate(), params)).thenReturn(processedHtml);

        sendMailServiceImpl.run(emailMessage);

        verify(templatePort).processTemplate("welcome-template", params);
        verify(mailSender).send("user@example.com", "Bem-vindo", processedHtml);
    }

    @Test
    void shouldHandleTemplatePortException() {
        EmailMessage emailMessage = new EmailMessage(
                "to@example.com",
                "subject",
                "invalid-template",
                null
        );

        when(templatePort.processTemplate(anyString(), any()))
                .thenThrow(new RuntimeException("Template não encontrado"));

        assertThrows(RuntimeException.class, () -> sendMailServiceImpl.run(emailMessage));

        verify(templatePort).processTemplate("invalid-template", null);
        verify(mailSender, never()).send(anyString(), anyString(), anyString());
    }

    @Test
    void shouldHandleMailSenderException() {
        EmailMessage emailMessage = new EmailMessage(
                "invalid@email",
                "subject",
                "template",
                null
        );

        String html = "<div>Content</div>";
        when(templatePort.processTemplate(anyString(), any())).thenReturn(html);
        doThrow(new RuntimeException("Erro ao enviar email"))
                .when(mailSender).send(anyString(), anyString(), anyString());

        assertThrows(RuntimeException.class, () -> sendMailServiceImpl.run(emailMessage));

        verify(templatePort).processTemplate("template", null);
        verify(mailSender).send("invalid@email", "subject", html);
    }

    @Test
    void shouldHandleEmptyTemplate() {
        EmailMessage emailMessage = new EmailMessage(
                "to@example.com",
                "subject",
                "empty-template",
                null
        );

        when(templatePort.processTemplate("empty-template", null)).thenReturn("");

        sendMailServiceImpl.run(emailMessage);

        verify(templatePort).processTemplate("empty-template", null);
        verify(mailSender).send("to@example.com", "subject", "");
    }
}