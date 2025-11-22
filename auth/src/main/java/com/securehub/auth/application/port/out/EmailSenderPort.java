package com.securehub.auth.application.port.out;

import com.securehub.auth.domain.email.EmailMessage;

public interface EmailSenderPort {
    void send(EmailMessage message);
}
