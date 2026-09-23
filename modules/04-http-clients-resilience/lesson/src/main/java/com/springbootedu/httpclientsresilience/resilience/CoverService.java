package com.springbootedu.httpclientsresilience.resilience;

import com.springbootedu.httpclientsresilience.catalog.CatalogApi;
import java.util.concurrent.atomic.AtomicInteger;
import org.springframework.resilience.annotation.ConcurrencyLimit;
import org.springframework.stereotype.Service;

/**
 * Lesson 3.7 — the cover service of our partner allows only two parallel downloads per client.
 */
@Service
public class CoverService {

    private final CatalogApi catalog;
    private final AtomicInteger inFlight = new AtomicInteger();
    private final AtomicInteger maxInFlight = new AtomicInteger();
    private final AtomicInteger completed = new AtomicInteger();

    public CoverService(CatalogApi catalog) {
        this.catalog = catalog;
    }

    // tag::concurrency-limit[]
    @ConcurrencyLimit(2)                                   // the 3rd caller waits (policy BLOCK, the default)
    public byte[] download(String isbn) {
        maxInFlight.accumulateAndGet(inFlight.incrementAndGet(), Math::max);
        try {
            return catalog.cover(isbn);
        } finally {
            inFlight.decrementAndGet();
            completed.incrementAndGet();
        }
    }
    // end::concurrency-limit[]

    public int maxInFlight() {
        return maxInFlight.get();
    }

    public int completed() {
        return completed.get();
    }
}
