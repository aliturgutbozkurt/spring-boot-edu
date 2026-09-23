package com.springbootedu.httpclientsresilience.resilience;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.stubbing.Scenario.STARTED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.springbootedu.httpclientsresilience.WireMockCatalogTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.HttpServerErrorException;

/**
 * Lesson 3.6 — @Retryable (Spring Framework 7): transient 5xx errors are retried with backoff.
 */
class PriceServiceTest extends WireMockCatalogTest {

    private static final String PRICE_URL = "/catalog/books/9780134685991/price";

    @Autowired
    PriceService prices;

    @Test
    void twoFailuresThenSuccess() {
        catalog.stubFor(get(PRICE_URL).inScenario("flaky").whenScenarioStateIs(STARTED)
                .willReturn(aResponse().withStatus(503)).willSetStateTo("failed once"));
        catalog.stubFor(get(PRICE_URL).inScenario("flaky").whenScenarioStateIs("failed once")
                .willReturn(aResponse().withStatus(503)).willSetStateTo("failed twice"));
        catalog.stubFor(get(PRICE_URL).inScenario("flaky").whenScenarioStateIs("failed twice")
                .willReturn(okJson("""
                        {"amount": 89.90, "currency": "TRY"}""")));

        assertThat(prices.currentPrice("9780134685991").amount()).isEqualByComparingTo("89.90");
        catalog.verify(3, getRequestedFor(urlEqualTo(PRICE_URL)));
    }

    @Test
    void givesUpAfterThreeRetries() {
        catalog.stubFor(get(PRICE_URL).willReturn(aResponse().withStatus(503)));

        assertThatThrownBy(() -> prices.currentPrice("9780134685991")).isInstanceOf(HttpServerErrorException.class);
        catalog.verify(4, getRequestedFor(urlEqualTo(PRICE_URL)));      // 1 call + 3 retries
    }

    @Test
    void clientErrorsAreNotRetried() {
        catalog.stubFor(get(PRICE_URL).willReturn(aResponse().withStatus(404)));

        assertThatThrownBy(() -> prices.currentPrice("9780134685991")).isNotNull();
        catalog.verify(1, getRequestedFor(urlEqualTo(PRICE_URL)));      // retrying a 404 would not help
    }

    @Test
    void theCallerFallsBackToTheLastSuccessfulPrice() {
        catalog.stubFor(get(PRICE_URL).willReturn(okJson("""
                {"amount": 70.00, "currency": "TRY"}""")));
        prices.currentPrice("9780134685991");                               // remembered as the last known price

        catalog.stubFor(get(PRICE_URL).willReturn(aResponse().withStatus(503)));   // then the catalog goes down

        assertThat(prices.priceOrLastKnown("9780134685991").amount()).isEqualByComparingTo("70.00");
    }
}
