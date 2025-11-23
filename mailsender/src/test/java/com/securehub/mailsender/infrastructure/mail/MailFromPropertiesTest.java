package com.securehub.mailsender.infrastructure.mail;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(MockitoExtension.class)
class MailFromPropertiesTest {

    @Test
    void setEmail_WhenValidEmail_ShouldSetEmail() {
        MailFromProperties properties = new MailFromProperties();
        String email = "test@example.com";

        properties.setEmail(email);

        assertEquals(email, properties.getEmail());
    }

    @Test
    void setName_WhenValidName_ShouldSetName() {
        MailFromProperties properties = new MailFromProperties();
        String name = "Test User";

        properties.setName(name);

        assertEquals(name, properties.getName());
    }

    @Test
    void getEmail_WhenNotSet_ShouldReturnNull() {
        MailFromProperties properties = new MailFromProperties();

        assertNull(properties.getEmail());
    }

    @Test
    void getName_WhenNotSet_ShouldReturnNull() {
        MailFromProperties properties = new MailFromProperties();

        assertNull(properties.getName());
    }

    @Test
    void setEmailAndName_WhenBothSet_ShouldReturnBothValues() {
        MailFromProperties properties = new MailFromProperties();
        String email = "sender@example.com";
        String name = "Mail Sender";

        properties.setEmail(email);
        properties.setName(name);

        assertEquals(email, properties.getEmail());
        assertEquals(name, properties.getName());
    }
}