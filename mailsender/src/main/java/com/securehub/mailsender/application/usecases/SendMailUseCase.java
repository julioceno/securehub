package com.securehub.mailsender.application.usecases;

import com.securehub.mailsender.domain.EmailMessage;

public interface SendMailUseCase {
    void run(EmailMessage emailMessage);
}
