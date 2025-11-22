package com.securehub.mailsender.infrastructure.template;

import com.securehub.mailsender.application.port.out.EmailTemplatePort;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Map;

@Service
public class ThymeleafEmailTemplateAdapter implements EmailTemplatePort {

    private final TemplateEngine templateEngine;

    public ThymeleafEmailTemplateAdapter(TemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

    @Override
    public String processTemplate(String templateName, Map<String, Object> params) {
        Context context = new Context();
        context.setVariables(params);
        return templateEngine.process(templateName, context);
    }
}