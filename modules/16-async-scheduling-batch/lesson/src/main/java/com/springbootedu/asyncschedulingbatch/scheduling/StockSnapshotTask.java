package com.springbootedu.asyncschedulingbatch.scheduling;

import java.util.concurrent.atomic.AtomicInteger;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Lesson 3.2 — runs again a fixed time after the previous run has finished.
 */
// tag::scheduled[]
@Component
public class StockSnapshotTask {

    private final AtomicInteger runs = new AtomicInteger();

    @Scheduled(fixedDelayString = "${bookstore.snapshot.interval}")    // 30s, 100ms, … from application.yaml
    void takeSnapshot() {
        runs.incrementAndGet();                                         // a real task would copy the stock levels
    }

    public int runs() {
        return runs.get();
    }
}
// end::scheduled[]
