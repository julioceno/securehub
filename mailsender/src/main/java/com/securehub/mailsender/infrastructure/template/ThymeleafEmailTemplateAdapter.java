package com.securehub.mailsender.infrastructure.template;

import com.securehub.mailsender.application.port.out.EmailTemplatePort;
import com.securehub.mailsender.application.util.CorrelationId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Map;

@Service
public class ThymeleafEmailTemplateAdapter implements EmailTemplatePort {
    private static final Logger log = LoggerFactory.getLogger(ThymeleafEmailTemplateAdapter.class);

    private final TemplateEngine templateEngine;

    public ThymeleafEmailTemplateAdapter(TemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

    @Override
    public String processTemplate(String templateName, Map<String, Object> params) {
        String correlationId = CorrelationId.get();
        log.debug("ThymeleafEmailTemplateAdapter.run - start - correlationId [{}] - template [{}]", correlationId, templateName);
        Context context = new Context();
        context.setVariables(params);
        log.debug("ThymeleafEmailTemplateAdapter.run - defined context - correlationId [{}]", correlationId);

        String body = templateEngine.process(templateName, context);
        log.debug("ThymeleafEmailTemplateAdapter.run - end - correlationId [{}] - template [{}]", correlationId, templateName);
        return body;
    }
}