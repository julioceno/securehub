package com.securehub.mailsender.infrastructure.mail;

import com.securehub.mailsender.application.util.CorrelationId;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JavaMailSenderAdapterTest {

    @Mock
    private JavaMailSender javaMailSender;

    @Mock
    private MimeMessage mimeMessage;

    @InjectMocks
    private JavaMailSenderAdapter adapter;

    private MockedStatic<CorrelationId> correlationIdMock;

    @BeforeEach
    void setUp() {
        correlationIdMock = mockStatic(CorrelationId.class);
    }

    @AfterEach
    void tearDown() {
        correlationIdMock.close();
    }

    @Test
    void send_WhenValidParameters_ShouldSendEmailSuccessfully() {
        String to = "test@example.com";
        String subject = "Test Subject";
        String body = "<html>Test Body</html>";
        String correlationId = "correlation-123";

        correlationIdMock.when(CorrelationId::get).thenReturn(correlationId);
        when(javaMailSender.createMimeMessage()).thenReturn(mimeMessage);

        adapter.send(to, subject, body);

        verify(javaMailSender).createMimeMessage();
        verify(javaMailSender).send(mimeMessage);
    }

    @Test
    void send_WhenCreateMimeMessageThrowsException_ShouldThrowRuntimeException() {
        String to = "test@example.com";
        String subject = "Test Subject";
        String body = "<html>Test Body</html>";
        String correlationId = "correlation-123";

        correlationIdMock.when(CorrelationId::get).thenReturn(correlationId);
        when(javaMailSender.createMimeMessage()).thenThrow(new RuntimeException("Mail server error"));

        assertThrows(RuntimeException.class, () ->
                adapter.send(to, subject, body));

        verify(javaMailSender).createMimeMessage();
        verify(javaMailSender, never()).send(any(MimeMessage.class));
    }

    @Test
    void send_WhenSendThrowsException_ShouldThrowRuntimeException() {
        String to = "test@example.com";
        String subject = "Test Subject";
        String body = "<html>Test Body</html>";
        String correlationId = "correlation-456";

        correlationIdMock.when(CorrelationId::get).thenReturn(correlationId);
        when(javaMailSender.createMimeMessage()).thenReturn(mimeMessage);
        doThrow(new RuntimeException("Send failed")).when(javaMailSender).send(mimeMessage);

        assertThrows(RuntimeException.class, () ->
                adapter.send(to, subject, body));

        verify(javaMailSender).createMimeMessage();
        verify(javaMailSender).send(mimeMessage);
    }
}