package com.springbootedu.testing.exercise2;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

/**
 * Exercise 2 — given: marks a shipment as dispatched in the background, after a variable delay (50–400 ms).
 */
public class ShipmentTracker {

    private volatile String status = "PENDING";

    public void dispatch() {
        long delay = ThreadLocalRandom.current().nextLong(50, 400);
        CompletableFuture.runAsync(() -> status = "DISPATCHED",
                CompletableFuture.delayedExecutor(delay, TimeUnit.MILLISECONDS));
    }

    public String status() {
        return status;
    }
}
