package com.springbootedu.httpclientsresilience.exercise3;

import java.util.concurrent.atomic.AtomicInteger;
import org.springframework.resilience.annotation.ConcurrencyLimit;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

/**
 * Exercise 3 — the partner allows 1 express and 2 standard quote requests at the same time.
 */
@Service
public class QuoteService {

    private final RestClient partner;
    private final AtomicInteger standardInFlight = new AtomicInteger();
    private final AtomicInteger maxStandardInFlight = new AtomicInteger();

    public QuoteService(RestClient partner) {
        this.partner = partner;
    }

    @ConcurrencyLimit(limit = 1, policy = ConcurrencyLimit.ThrottlePolicy.REJECT)   // refuse instead of queueing
    public Quote expressQuote(String isbn) {
        return partner.get().uri("/quotes/express/{isbn}", isbn).retrieve().body(Quote.class);
    }

    @ConcurrencyLimit(2)                                                              // queue (BLOCK)
    public Quote standardQuote(String isbn) {
        maxStandardInFlight.accumulateAndGet(standardInFlight.incrementAndGet(), Math::max);
        try {
            return partner.get().uri("/quotes/standard/{isbn}", isbn).retrieve().body(Quote.class);
        } finally {
            standardInFlight.decrementAndGet();
        }
    }

    public int maxStandardInFlight() {
        return maxStandardInFlight.get();
    }
}
