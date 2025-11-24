package com.securehub.mailsender.adapter.in;

import com.securehub.mailsender.application.usecases.SendMailUseCase;
import com.securehub.mailsender.application.util.CorrelationId;
import com.securehub.mailsender.domain.EmailMessage;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.kafka.annotation.BackOff;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

@Service
@AllArgsConstructor
@Slf4j
public class EmailConsumer {
    private final ObjectMapper objectMapper;
    private final SendMailUseCase sendMailUseCase;

    @RetryableTopic(
            attempts = "3",
            backOff = @BackOff(delay = 2000, multiplier = 2.0),
            dltTopicSuffix = "-dlt",
            autoCreateTopics = "true",
            include = {Exception.class}
    )
    @KafkaListener(
            topics = "${kafka.consumer.email.topic}",
            groupId = "${kafka.consumer.group-id}",
            concurrency = "${kafka.consumer.email.concurrency}"
    )
    public void listen(
            @Payload String emailMessage,
            @Header(CorrelationId.HEADER_NAME) String correlationId
        ) {
        try {
            MDC.put(CorrelationId.HEADER_NAME, correlationId);

            EmailMessage email = objectMapper.readValue(emailMessage, EmailMessage.class);
            sendMailUseCase.run(email);
        } catch (Exception e) {
            log.error("EmailConsumer.listen - Error - correlationId [{}] - error:", correlationId, e);
            throw e;
        } finally {
            MDC.clear();
        }
    }
}
