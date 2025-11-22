package com.securehub.auth.infrastructure.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.securehub.auth.application.exception.BadRequestException;
import com.securehub.auth.application.port.out.EmailSenderPort;
import com.securehub.auth.application.util.CorrelationId;
import com.securehub.auth.domain.email.EmailMessage;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

@Component
public class KafkaEmailProducer implements EmailSenderPort {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ObjectMapper mapper = new ObjectMapper();

    public KafkaEmailProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void send(EmailMessage emailMessage) {
        try {
            String payload = mapper.writeValueAsString(emailMessage);
            Message<String> message = MessageBuilder.withPayload(payload)
                    .setHeader(KafkaHeaders.TOPIC, "mail")
                    .setHeader(CorrelationId.HEADER_NAME, CorrelationId.get())
                    .build();

            kafkaTemplate.send(message);
        } catch (Exception ex) {
            throw new BadRequestException("An error occurred while sending mail");
        }
    }
}
