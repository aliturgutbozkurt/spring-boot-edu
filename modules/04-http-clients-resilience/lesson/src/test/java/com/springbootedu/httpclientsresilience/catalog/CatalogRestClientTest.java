package com.springbootedu.httpclientsresilience.catalog;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.matching;
import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.springbootedu.httpclientsresilience.WireMockCatalogTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Lessons 3.1–3.3 — RestClient: happy path, error mapping and a shared interceptor.
 */
class CatalogRestClientTest extends WireMockCatalogTest {

    @Autowired
    CatalogRestClient client;

    // tag::wiremock[]
    @Test
    void readsABook() {
        catalog.stubFor(get("/catalog/books/9780134685991").willReturn(okJson(EFFECTIVE_JAVA)));   // fake response

        assertThat(client.find("9780134685991"))
                .isEqualTo(new BookInfo("9780134685991", "Effective Java", java.util.List.of("Joshua Bloch"), 412));
    }
    // end::wiremock[]

    @Test
    void a404BecomesADomainException() {
        catalog.stubFor(get("/catalog/books/9780000000000").willReturn(aResponse().withStatus(404)));

        assertThatThrownBy(() -> client.find("9780000000000"))
                .isInstanceOf(BookInfoNotFoundException.class)
                .hasMessageContaining("9780000000000");
    }

    @Test
    void a5xxBecomesCatalogUnavailable() {
        catalog.stubFor(get("/catalog/books/9780134685991").willReturn(aResponse().withStatus(503)));

        assertThatThrownBy(() -> client.find("9780134685991"))
                .isInstanceOf(CatalogUnavailableException.class)
                .hasMessageContaining("503");
    }

    @Test
    void everyRequestCarriesACorrelationIdAndUserAgent() {
        catalog.stubFor(get("/catalog/books/9780134685991").willReturn(okJson(EFFECTIVE_JAVA)));

        client.find("9780134685991");

        catalog.verify(getRequestedFor(urlEqualTo("/catalog/books/9780134685991"))
                .withHeader("X-Request-Id", matching("[0-9a-f-]{36}"))
                .withHeader("User-Agent", equalTo("bookstore/1.0")));
    }
}
