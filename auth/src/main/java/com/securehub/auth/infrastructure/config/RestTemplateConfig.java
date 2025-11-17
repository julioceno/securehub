package com.securehub.auth.infrastructure.config;

import com.securehub.auth.application.util.CorrelationId;
import org.slf4j.MDC;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        ClientHttpRequestInterceptor interceptor = (request, body, execution) -> {
            String id = MDC.get(CorrelationId.HEADER_NAME);
            if (id != null) {
                request.getHeaders().set(CorrelationId.HEADER_NAME, id);
            }

            return execution.execute(request, body);
        };

        return builder.additionalInterceptors(interceptor).build();
    }
}
