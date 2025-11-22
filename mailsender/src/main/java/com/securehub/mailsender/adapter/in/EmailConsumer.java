package com.securehub.mailsender.adapter.in;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class EmailConsumer {

    // TODO: get configuration from application.yaml
    @KafkaListener(topics = "email-topic", groupId = "mail-sender-group")
    public void listen(String message) {
        System.out.println("Mensagem recebida: " + message);
    }
}
