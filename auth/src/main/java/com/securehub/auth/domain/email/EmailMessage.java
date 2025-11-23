package com.securehub.auth.domain.email;

import java.util.Map;

public class EmailMessage {
    private String to;
    private String subject;
    private String template;
    private Map<String, Object> params;

    public EmailMessage() {
    }

    public EmailMessage(String to, String subject, String template, Map<String, Object> params) {
        this.to = to;
        this.subject = subject;
        this.template = template;
        this.params = params;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getTemplate() {
        return template;
    }

    public void setTemplate(String template) {
        this.template = template;
    }

    public Map<String, Object> getParams() {
        return params;
    }

    public void setParams(Map<String, Object> params) {
        this.params = params;
    }
}
