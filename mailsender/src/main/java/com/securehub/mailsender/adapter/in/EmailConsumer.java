package com.securehub.mailsender.adapter.in;

import com.securehub.mailsender.application.util.CorrelationId;
import com.securehub.mailsender.domain.EmailMessage;
import org.slf4j.MDC;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

@Service
public class EmailConsumer {

    private final ObjectMapper objectMapper;

    public EmailConsumer(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    // TODO: get configuration from application.yaml
    @KafkaListener(topics = "email-topic", groupId = "mail-sender-group")
    public void listen(
            @Payload String emailMessage,
            @Header(CorrelationId.HEADER_NAME) String correlationId
    ) {
        try {
            MDC.put(CorrelationId.HEADER_NAME, correlationId);
            EmailMessage email = objectMapper.readValue(emailMessage, EmailMessage.class);

            IO.println("Mensagem" + correlationId);
        } finally {
            MDC.clear();
        }
    }
}
