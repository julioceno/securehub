package com.securehub.mailsender.infrastructure.config;

import com.securehub.mailsender.application.port.out.EmailTemplatePort;
import com.securehub.mailsender.application.port.out.MailSenderPort;
import com.securehub.mailsender.application.service.SendMailServiceImpl;
import com.securehub.mailsender.application.usecases.SendMailUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
class BeansConfigTest {

    @Mock
    private MailSenderPort mailSenderPort;

    @Mock
    private EmailTemplatePort emailTemplatePort;

    private BeansConfig beansConfig;

    @BeforeEach
    void setUp() {
        beansConfig = new BeansConfig();
    }

    @Test
    void sendMailUseCase_WhenValidDependencies_ShouldReturnSendMailServiceImpl() {
        SendMailUseCase result = beansConfig.sendMailUseCase(mailSenderPort, emailTemplatePort);

        assertNotNull(result);
        assertInstanceOf(SendMailServiceImpl.class, result);
    }
}