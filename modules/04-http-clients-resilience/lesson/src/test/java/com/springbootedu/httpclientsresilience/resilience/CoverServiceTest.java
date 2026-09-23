package com.springbootedu.httpclientsresilience.resilience;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static org.assertj.core.api.Assertions.assertThat;

import com.springbootedu.httpclientsresilience.WireMockCatalogTest;
import java.util.concurrent.Executors;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Lesson 3.7 — @ConcurrencyLimit: at most two downloads at the same time, even with many callers.
 */
class CoverServiceTest extends WireMockCatalogTest {

    @Autowired
    CoverService covers;

    @Test
    void neverMoreThanTwoCallsInFlight() {
        catalog.stubFor(com.github.tomakehurst.wiremock.client.WireMock.get(urlPathMatching("/catalog/books/.*/cover"))
                .willReturn(aResponse().withBody(new byte[] {1, 2, 3}).withFixedDelay(150)));

        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            for (int i = 0; i < 8; i++) {
                String isbn = "97800000000" + i;
                executor.submit(() -> covers.download(isbn));
            }
        }

        assertThat(covers.completed()).isEqualTo(8);
        assertThat(covers.maxInFlight()).isEqualTo(2);
    }
}
