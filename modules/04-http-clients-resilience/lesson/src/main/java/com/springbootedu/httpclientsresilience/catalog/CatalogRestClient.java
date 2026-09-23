package com.springbootedu.httpclientsresilience.catalog;

import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

/**
 * Lessons 3.1–3.2 — a hand-written client on top of RestClient.
 */
@Component
public class CatalogRestClient {

    private final RestClient restClient;

    // tag::rest-client[]
    public CatalogRestClient(RestClient.Builder builder, CatalogProperties properties) {
        this.restClient = builder                          // Boot's builder: timeouts, customizers, JSON
                .baseUrl(properties.baseUrl())
                .build();
    }

    public BookInfo find(String isbn) {
        return restClient.get()
                .uri("/catalog/books/{isbn}", isbn)        // URI template: the value is encoded safely
                .retrieve()
                .onStatus(status -> status.value() == 404, (request, response) -> {
                    throw new BookInfoNotFoundException(isbn);
                })
                .onStatus(HttpStatusCode::is5xxServerError, (request, response) -> {
                    throw new CatalogUnavailableException("Catalog answered " + response.getStatusCode());
                })
                .body(BookInfo.class);                     // JSON → record
    }
    // end::rest-client[]
}
