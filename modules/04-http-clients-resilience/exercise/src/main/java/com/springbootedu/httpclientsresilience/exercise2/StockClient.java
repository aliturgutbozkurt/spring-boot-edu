package com.springbootedu.httpclientsresilience.exercise2;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

/**
 * Exercise 2 — asks the partner's stock service; transient 5xx failures are retried.
 */
@Component
public class StockClient {

    private final RestClient partner;

    public StockClient(RestClient partner) {
        this.partner = partner;
    }

    // TODO 2b: retry server errors (5xx) only, at most 2 retries, 50 ms apart
    public int available(String isbn) {
        StockLevel level = partner.get().uri("/stock/{isbn}", isbn).retrieve().body(StockLevel.class);
        return level == null ? 0 : level.available();
    }
}
