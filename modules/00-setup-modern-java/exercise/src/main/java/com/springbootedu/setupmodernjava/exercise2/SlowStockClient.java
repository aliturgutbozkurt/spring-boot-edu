package com.springbootedu.setupmodernjava.exercise2;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Exercise 2 — given: simulates a slow stock service and records how it was called.
 */
public class SlowStockClient implements StockClient {

    private final Duration latency;
    private final AtomicInteger calls = new AtomicInteger();
    private final AtomicBoolean allVirtual = new AtomicBoolean(true);

    public SlowStockClient(Duration latency) {
        this.latency = latency;
    }

    public static int expectedStock(String isbn) {
        return Math.floorMod(isbn.hashCode(), 50);
    }

    @Override
    public int stockOf(String isbn) throws InterruptedException {
        calls.incrementAndGet();
        if (!Thread.currentThread().isVirtual()) {
            allVirtual.set(false);
        }
        Thread.sleep(latency);
        return expectedStock(isbn);
    }

    public int calls() {
        return calls.get();
    }

    public boolean allCallsOnVirtualThreads() {
        return allVirtual.get();
    }
}
