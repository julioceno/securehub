package com.securehub.mailsender.infrastructure.template;

import com.securehub.mailsender.application.util.CorrelationId;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ThymeleafEmailTemplateAdapterTest {

    @Mock
    private TemplateEngine templateEngine;

    @InjectMocks
    private ThymeleafEmailTemplateAdapter adapter;

    private MockedStatic<CorrelationId> correlationIdMock;

    @BeforeEach
    void setUp() {
        correlationIdMock = mockStatic(CorrelationId.class);
    }

    @AfterEach
    void tearDown() {
        correlationIdMock.close();
    }

    @Test
    void processTemplate_WhenValidInput_ShouldReturnProcessedTemplate() {
        String templateName = "email-template";
        Map<String, Object> params = Map.of("name", "John", "email", "john@example.com");
        String correlationId = "correlation-123";
        String expectedBody = "<html>Processed template</html>";

        correlationIdMock.when(CorrelationId::get).thenReturn(correlationId);
        when(templateEngine.process(eq(templateName), any(Context.class))).thenReturn(expectedBody);

        String result = adapter.processTemplate(templateName, params);

        assertEquals(expectedBody, result);
        verify(templateEngine).process(eq(templateName), any(Context.class));
    }

    @Test
    void processTemplate_WhenEmptyParams_ShouldProcessTemplate() {
        String templateName = "simple-template";
        Map<String, Object> params = Map.of();
        String correlationId = "correlation-456";
        String expectedBody = "<html>Simple template</html>";

        correlationIdMock.when(CorrelationId::get).thenReturn(correlationId);
        when(templateEngine.process(eq(templateName), any(Context.class))).thenReturn(expectedBody);

        String result = adapter.processTemplate(templateName, params);

        assertEquals(expectedBody, result);
        verify(templateEngine).process(eq(templateName), any(Context.class));
    }

    @Test
    void processTemplate_WhenTemplateEngineThrowsException_ShouldPropagateException() {
        String templateName = "invalid-template";
        Map<String, Object> params = Map.of("key", "value");
        String correlationId = "correlation-789";

        correlationIdMock.when(CorrelationId::get).thenReturn(correlationId);
        when(templateEngine.process(eq(templateName), any(Context.class)))
                .thenThrow(new RuntimeException("Template not found"));

        assertThrows(RuntimeException.class, () ->
                adapter.processTemplate(templateName, params));

        verify(templateEngine).process(eq(templateName), any(Context.class));
    }
}