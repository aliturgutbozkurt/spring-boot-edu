package com.springbootedu.httpclientsresilience.exercise3;

import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static org.assertj.core.api.Assertions.assertThat;

import com.springbootedu.httpclientsresilience.PartnerServerTest;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Exercise 3 — protecting the partner: queue (BLOCK) or refuse (REJECT) when too many calls run at once.
 */
class Exercise3Test extends PartnerServerTest {

    @Autowired
    QuoteFacade quotes;

    @Autowired
    QuoteService quoteService;

    @Test
    void onlyOneExpressQuoteAtATimeTheOthersFallBackToStandard() throws Exception {
        partner.stubFor(get(urlPathMatching("/quotes/(express|standard)/.*"))
                .willReturn(okJson("{\"price\": 49.90}").withFixedDelay(300)));

        List<Future<String>> results = new ArrayList<>();
        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            for (int i = 0; i < 4; i++) {
                results.add(executor.submit(() -> quotes.expressOrStandard("9780134685991")));
                Thread.sleep(20);                 // the first call surely holds the express slot
            }
        }

        List<String> kinds = new ArrayList<>();
        for (Future<String> result : results) {
            kinds.add(result.get());
        }
        assertThat(kinds).containsExactlyInAnyOrder("express", "standard", "standard", "standard");
    }

    @Test
    void standardQuotesRunAtMostTwoAtATime() {
        partner.stubFor(get(urlPathMatching("/quotes/standard/.*"))
                .willReturn(okJson("{\"price\": 29.90}").withFixedDelay(100)));

        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            for (int i = 0; i < 6; i++) {
                executor.submit(() -> quoteService.standardQuote("9780134685991"));
            }
        }

        assertThat(quoteService.maxStandardInFlight()).isEqualTo(2);
    }
}
