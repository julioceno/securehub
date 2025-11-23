package com.securehub.mailsender.adapter.in;

import com.securehub.mailsender.application.usecases.SendMailUseCase;
import com.securehub.mailsender.application.util.CorrelationId;
import com.securehub.mailsender.domain.EmailMessage;
import lombok.AllArgsConstructor;
import org.slf4j.MDC;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

@Service
@AllArgsConstructor
public class EmailConsumer {
    private final ObjectMapper objectMapper;
    private final SendMailUseCase sendMailUseCase;

    // TODO: get configuration from application.yaml
    @KafkaListener(
            topics = "email-topic",
            groupId = "mail-sender-group"
    )
    public void listen(
            @Payload String emailMessage,
            @Header(CorrelationId.HEADER_NAME) String correlationId
    ) {
        try {
            MDC.put(CorrelationId.HEADER_NAME, correlationId);
            EmailMessage email = objectMapper.readValue(emailMessage, EmailMessage.class);
            sendMailUseCase.run(email);
        } finally {
            MDC.clear();
        }
    }
}
