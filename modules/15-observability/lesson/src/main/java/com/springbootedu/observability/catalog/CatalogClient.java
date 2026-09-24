package com.springbootedu.observability.catalog;

import java.util.Map;
import java.util.Objects;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

/**
 * Lesson 3.5 — a RestClient built from Boot's RestClient.Builder is observed: it creates a client span and
 * sends the trace context in the "traceparent" header.
 */
// tag::client[]
@Component
public class CatalogClient {

    private final RestClient.Builder http;
    private final Environment environment;

    public CatalogClient(RestClient.Builder http, Environment environment) {   // the builder, not RestClient.create()
        this.http = http;
        this.environment = environment;
    }

    public String titleOf(String isbn) {
        String port = environment.getProperty("local.server.port", "8080");
        Map<?, ?> book = http.baseUrl("http://localhost:" + port).build()
                .get().uri("/api/catalog/{isbn}", isbn)
                .retrieve().body(Map.class);
        return String.valueOf(Objects.requireNonNull(book).get("title"));
    }
}
// end::client[]
