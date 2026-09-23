package com.springbootedu.httpclientsresilience.catalog;

import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
import static org.assertj.core.api.Assertions.assertThat;

import com.springbootedu.httpclientsresilience.WireMockCatalogTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Lesson 3.4 — an HTTP interface client: an annotated interface, no implementation code.
 */
class CatalogApiTest extends WireMockCatalogTest {

    @Autowired
    CatalogApi api;

    @Test
    void theInterfaceIsBackedByAGeneratedClient() {
        catalog.stubFor(get("/catalog/books/9780134685991").willReturn(okJson(EFFECTIVE_JAVA)));

        assertThat(api.find("9780134685991").title()).isEqualTo("Effective Java");
    }

    @Test
    void readsThePrice() {
        catalog.stubFor(get("/catalog/books/9780134685991/price").willReturn(okJson("""
                {"amount": 89.90, "currency": "TRY"}""")));

        assertThat(api.price("9780134685991").amount()).isEqualByComparingTo("89.90");
    }
}
