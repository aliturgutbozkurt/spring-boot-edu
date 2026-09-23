package com.springbootedu.httpclientsresilience.exercise2;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.stubbing.Scenario.STARTED;
import static org.assertj.core.api.Assertions.assertThat;

import com.springbootedu.httpclientsresilience.PartnerServerTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Exercise 2 — retries for transient failures, a fallback when they do not help.
 */
class Exercise2Test extends PartnerServerTest {

    private static final String STOCK_URL = "/stock/9780134685991";

    @Autowired
    StockFacade stock;

    @Test
    void aTransientFailureIsRetried() {
        partner.stubFor(get(STOCK_URL).inScenario("flaky").whenScenarioStateIs(STARTED)
                .willReturn(aResponse().withStatus(502)).willSetStateTo("recovered"));
        partner.stubFor(get(STOCK_URL).inScenario("flaky").whenScenarioStateIs("recovered")
                .willReturn(okJson("{\"isbn\": \"9780134685991\", \"available\": 7}")));

        assertThat(stock.availableOrUnknown("9780134685991")).isEqualTo(7);
        partner.verify(2, getRequestedFor(urlEqualTo(STOCK_URL)));
    }

    @Test
    void afterTwoRetriesTheFallbackAnswersUnknown() {
        partner.stubFor(get(STOCK_URL).willReturn(aResponse().withStatus(503)));

        assertThat(stock.availableOrUnknown("9780134685991")).isEqualTo(StockFacade.UNKNOWN);
        partner.verify(3, getRequestedFor(urlEqualTo(STOCK_URL)));      // 1 call + 2 retries
    }

    @Test
    void aClientErrorIsNotRetried() {
        partner.stubFor(get(STOCK_URL).willReturn(aResponse().withStatus(404)));

        assertThat(stock.availableOrUnknown("9780134685991")).isEqualTo(StockFacade.UNKNOWN);
        partner.verify(1, getRequestedFor(urlEqualTo(STOCK_URL)));
    }
}
