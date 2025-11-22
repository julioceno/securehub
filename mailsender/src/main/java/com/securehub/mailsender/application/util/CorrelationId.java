package com.securehub.mailsender.application.util;

import org.slf4j.MDC;

public final class CorrelationId {
    public static final String HEADER_NAME = "X-Correlation-Id";

    private CorrelationId() {}

    public static String get() {
        return MDC.get(HEADER_NAME);
    }
}
