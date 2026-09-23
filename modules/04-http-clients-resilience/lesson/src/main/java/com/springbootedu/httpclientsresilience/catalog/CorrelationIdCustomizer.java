package com.springbootedu.httpclientsresilience.catalog;

import java.util.UUID;
import org.springframework.boot.restclient.RestClientCustomizer;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

/**
 * Lesson 3.3 — applied by Boot to every RestClient.Builder it hands out: one place for cross-cutting headers.
 */
// tag::customizer[]
@Component
public class CorrelationIdCustomizer implements RestClientCustomizer {

    @Override
    public void customize(RestClient.Builder builder) {
        builder.defaultHeader(HttpHeaders.USER_AGENT, "bookstore/1.0")
                .requestInterceptor((request, body, execution) -> {
                    request.getHeaders().add("X-Request-Id", UUID.randomUUID().toString());   // trace a call across services
                    return execution.execute(request, body);
                });
    }
}
// end::customizer[]
