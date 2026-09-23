package com.springbootedu.httpclientsresilience.catalog;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

/**
 * Lesson 3.5 — the same request with the reactive WebClient, for comparison.
 */
// tag::web-client[]
@Component
public class ReactiveCatalogClient {

    private final WebClient webClient;

    public ReactiveCatalogClient(WebClient.Builder builder, CatalogProperties properties) {
        this.webClient = builder.baseUrl(properties.baseUrl()).build();
    }

    public Mono<BookInfo> find(String isbn) {
        return webClient.get()
                .uri("/catalog/books/{isbn}", isbn)
                .retrieve()
                .bodyToMono(BookInfo.class);               // nothing happens until someone subscribes
    }
}
// end::web-client[]
