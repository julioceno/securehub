package com.securehub.mailsender.adapter.in;

import com.securehub.mailsender.application.usecases.SendMailUseCase;
import com.securehub.mailsender.application.util.CorrelationId;
import com.securehub.mailsender.domain.EmailMessage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.MDC;
import tools.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailConsumerTest {

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private SendMailUseCase sendMailUseCase;

    @InjectMocks
    private EmailConsumer emailConsumer;

    private MockedStatic<MDC> mdcMock;

    @BeforeEach
    void setUp() {
        mdcMock = mockStatic(MDC.class);
    }

    @AfterEach
    void tearDown() {
        mdcMock.close();
    }

    @Test
    void listen_WhenValidMessage_ShouldProcessSuccessfully() {
        String emailMessageJson = "{\"to\":\"test@example.com\",\"subject\":\"Test\"}";
        String correlationId = "correlation-123";
        EmailMessage emailMessage = new EmailMessage();

        when(objectMapper.readValue(emailMessageJson, EmailMessage.class)).thenReturn(emailMessage);

        emailConsumer.listen(emailMessageJson, correlationId);

        mdcMock.verify(() -> MDC.put(CorrelationId.HEADER_NAME, correlationId));
        verify(objectMapper).readValue(emailMessageJson, EmailMessage.class);
        verify(sendMailUseCase).run(emailMessage);
        mdcMock.verify(MDC::clear);
    }

    @Test
    void listen_WhenObjectMapperThrowsException_ShouldClearMDC() {
        String emailMessageJson = "invalid-json";
        String correlationId = "correlation-123";

        when(objectMapper.readValue(emailMessageJson, EmailMessage.class))
                .thenThrow(new RuntimeException("JSON parsing error"));

        assertThrows(RuntimeException.class, () ->
                emailConsumer.listen(emailMessageJson, correlationId));

        mdcMock.verify(() -> MDC.put(CorrelationId.HEADER_NAME, correlationId));
        verify(sendMailUseCase, never()).run(any());
        mdcMock.verify(MDC::clear);
    }

    @Test
    void listen_WhenSendMailUseCaseThrowsException_ShouldClearMDC() {
        String emailMessageJson = "{\"to\":\"test@example.com\",\"subject\":\"Test\"}";
        String correlationId = "correlation-123";
        EmailMessage emailMessage = new EmailMessage();

        when(objectMapper.readValue(emailMessageJson, EmailMessage.class)).thenReturn(emailMessage);
        doThrow(new RuntimeException("Send mail error")).when(sendMailUseCase).run(emailMessage);

        assertThrows(RuntimeException.class, () ->
                emailConsumer.listen(emailMessageJson, correlationId));

        mdcMock.verify(() -> MDC.put(CorrelationId.HEADER_NAME, correlationId));
        verify(objectMapper).readValue(emailMessageJson, EmailMessage.class);
        verify(sendMailUseCase).run(emailMessage);
        mdcMock.verify(MDC::clear);
    }
}