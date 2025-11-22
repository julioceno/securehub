package com.securehub.mailsender.application.port.out;

import java.util.Map;

public interface EmailTemplatePort {
    String processTemplate(String template, Map<String, Object> params);
}
