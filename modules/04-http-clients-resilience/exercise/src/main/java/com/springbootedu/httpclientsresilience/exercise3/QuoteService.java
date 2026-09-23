package com.springbootedu.httpclientsresilience.exercise3;

import java.util.concurrent.atomic.AtomicInteger;
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

    // TODO 3a: at most 1 call at a time; a second caller must be REFUSED immediately (not queued)
    public Quote expressQuote(String isbn) {
        return partner.get().uri("/quotes/express/{isbn}", isbn).retrieve().body(Quote.class);
    }

    // TODO 3b: at most 2 calls at a time; further callers wait
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
