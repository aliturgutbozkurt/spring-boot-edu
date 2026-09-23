package com.springbootedu.httpclientsresilience.exercise2;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;

/**
 * Exercise 2 — the caller: returns {@link #UNKNOWN} when the stock service cannot answer.
 */
@Service
public class StockFacade {

    public static final int UNKNOWN = -1;

    private final StockClient client;

    public StockFacade(StockClient client) {
        this.client = client;
    }

    public int availableOrUnknown(String isbn) {
        try {
            return client.available(isbn);
        } catch (RestClientException exception) {
            return UNKNOWN;
        }
    }
}
