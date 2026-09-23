package com.springbootedu.hazelcast.pricing;

import java.math.BigDecimal;
import java.util.concurrent.atomic.AtomicInteger;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

/**
 * Lesson 3.4 — a slow price lookup behind {@code @Cacheable}. The cache is the IMap "prices".
 */
// tag::cacheable[]
@Service
public class PriceService {

    private final AtomicInteger calls = new AtomicInteger();

    @Cacheable("prices")
    public BigDecimal priceOf(String isbn) {
        calls.incrementAndGet();
        sleep(300);                                                   // pretend to ask a slow pricing system
        return new BigDecimal("89.90");
    }
    // end::cacheable[]

    public int calls() {
        return calls.get();
    }

    public void resetCalls() {
        calls.set(0);
    }

    private static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
