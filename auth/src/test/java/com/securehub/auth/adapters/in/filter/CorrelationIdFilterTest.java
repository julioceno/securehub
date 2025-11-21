package com.securehub.auth.adapters.in.filter;

import com.securehub.auth.application.util.CorrelationId;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

class CorrelationIdFilterTest {

    private final CorrelationIdFilter filter = new CorrelationIdFilter();

    @AfterEach
    void tearDown() {
        MDC.clear();
    }

    @Test
    void shouldUseHeaderCorrelationId() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        String provided = "test-correlation-id-123";
        request.addHeader(CorrelationId.HEADER_NAME, provided);

        AtomicReference<String> captured = new AtomicReference<>();

        FilterChain chain = new FilterChain() {
            @Override
            public void doFilter(ServletRequest req, ServletResponse res) {
                captured.set(MDC.get(CorrelationId.HEADER_NAME));
            }
        };

        filter.doFilter(request, response, chain);

        assertEquals(provided, captured.get(), "should contain correlation-id header");
        assertNull(response.getHeader(CorrelationId.HEADER_NAME));
        assertNull(MDC.get(CorrelationId.HEADER_NAME));
    }

    @Test
    void shouldGenerateCorrelationIdWhenMissing() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        AtomicReference<String> captured = new AtomicReference<>();

        FilterChain chain = new FilterChain() {
            @Override
            public void doFilter(ServletRequest req, ServletResponse res) {
                captured.set(MDC.get(CorrelationId.HEADER_NAME));
            }
        };

        filter.doFilter(request, response, chain);

        String generated = captured.get();
        assertNotNull(generated, "Should generate an correlation id when the header does not exists");
        assertEquals(generated, response.getHeader(CorrelationId.HEADER_NAME));
        assertNull(MDC.get(CorrelationId.HEADER_NAME));
    }
}

