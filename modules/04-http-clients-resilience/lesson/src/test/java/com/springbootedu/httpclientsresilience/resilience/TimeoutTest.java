package com.springbootedu.httpclientsresilience.resilience;

import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.springbootedu.httpclientsresilience.WireMockCatalogTest;
import com.springbootedu.httpclientsresilience.catalog.CatalogRestClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.client.ResourceAccessException;

/**
 * Lesson 3.8 — a read timeout turns a hanging remote call into a fast, clear failure.
 */
@TestPropertySource(properties = "spring.http.clients.read-timeout=300ms")
class TimeoutTest extends WireMockCatalogTest {

    @Autowired
    CatalogRestClient client;

    @Test
    void aSlowResponseTimesOut() {
        catalog.stubFor(get("/catalog/books/9780134685991").willReturn(okJson(EFFECTIVE_JAVA).withFixedDelay(2_000)));

        long start = System.nanoTime();
        assertThatThrownBy(() -> client.find("9780134685991")).isInstanceOf(ResourceAccessException.class);
        org.assertj.core.api.Assertions.assertThat((System.nanoTime() - start) / 1_000_000).isLessThan(1_500);
    }
}
