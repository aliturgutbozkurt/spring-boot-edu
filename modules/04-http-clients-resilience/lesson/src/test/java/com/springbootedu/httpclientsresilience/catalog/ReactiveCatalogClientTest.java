package com.springbootedu.httpclientsresilience.catalog;

import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
import static org.assertj.core.api.Assertions.assertThat;

import com.springbootedu.httpclientsresilience.WireMockCatalogTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Lesson 3.5 — the same call with WebClient returns a Mono.
 */
class ReactiveCatalogClientTest extends WireMockCatalogTest {

    @Autowired
    ReactiveCatalogClient client;

    @Test
    void returnsAMonoThatEmitsTheBook() {
        catalog.stubFor(get("/catalog/books/9780134685991").willReturn(okJson(EFFECTIVE_JAVA)));

        assertThat(client.find("9780134685991").block()).extracting(BookInfo::pageCount).isEqualTo(412);
    }
}
