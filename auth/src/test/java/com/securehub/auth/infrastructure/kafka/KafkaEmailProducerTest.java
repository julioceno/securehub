package com.securehub.auth.infrastructure.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.securehub.auth.application.exception.BadRequestException;
import com.securehub.auth.application.util.CorrelationId;
import com.securehub.auth.domain.email.EmailMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class KafkaEmailProducerTest {

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    private KafkaEmailProducer kafkaEmailProducer;

    @BeforeEach
    void setUp() {
        kafkaEmailProducer = new KafkaEmailProducer(kafkaTemplate);
    }

    @Test
    void send_shouldSendMessageSuccessfully() {
        EmailMessage emailMessage = new EmailMessage("test@example.com", "Subject", "Body", null);
        String correlationId = "test-correlation-id";

        try (MockedStatic<CorrelationId> correlationIdMock = mockStatic(CorrelationId.class)) {
            correlationIdMock.when(CorrelationId::get).thenReturn(correlationId);

            kafkaEmailProducer.send(emailMessage);

            ArgumentCaptor<Message> messageCaptor = ArgumentCaptor.forClass(Message.class);
            verify(kafkaTemplate).send(messageCaptor.capture());

            Message<String> capturedMessage = messageCaptor.getValue();
            assertEquals("email-topic", capturedMessage.getHeaders().get(KafkaHeaders.TOPIC));
            assertEquals(correlationId, capturedMessage.getHeaders().get(CorrelationId.HEADER_NAME));
            assertNotNull(capturedMessage.getPayload());
        }
    }

    @Test
    void send_shouldThrowBadRequestException_whenSerializationFails() throws Exception {
        EmailMessage emailMessage = new EmailMessage("test@example.com", "Subject", "Body", null);

        KafkaEmailProducer spyProducer = spy(kafkaEmailProducer);
        ObjectMapper mockMapper = mock(ObjectMapper.class);

        when(mockMapper.writeValueAsString(any())).thenThrow(JsonProcessingException.class);

        java.lang.reflect.Field mapperField = KafkaEmailProducer.class.getDeclaredField("mapper");
        mapperField.setAccessible(true);
        mapperField.set(spyProducer, mockMapper);

        assertThrows(BadRequestException.class, () -> spyProducer.send(emailMessage));
    }

    @Test
    void send_shouldThrowBadRequestException_whenKafkaTemplateFails() {
        EmailMessage emailMessage = new EmailMessage("test@example.com", "Subject", "Body", null);
        String correlationId = "test-correlation-id";

        try (MockedStatic<CorrelationId> correlationIdMock = mockStatic(CorrelationId.class)) {
            correlationIdMock.when(CorrelationId::get).thenReturn(correlationId);
            doThrow(new RuntimeException("Kafka error")).when(kafkaTemplate).send(any(Message.class));

            BadRequestException exception = assertThrows(BadRequestException.class,
                    () -> kafkaEmailProducer.send(emailMessage));

            assertEquals("An error occurred while sending mail", exception.getMessage());
        }
    }
}