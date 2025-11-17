package com.securehub.auth.application.util;

import org.slf4j.MDC;

import java.util.Optional;

public final class CorrelationId {
    public static final String HEADER_NAME = "X-Correlation-Id";

    private CorrelationId() {}

    public static Optional<String> get() {
        return Optional.ofNullable(MDC.get(HEADER_NAME));
    }
}
