package com.springbootedu.asyncschedulingbatch.async;

import java.math.BigDecimal;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * Lesson 3.1 — a slow remote price service. @Async makes each call return at once with a CompletableFuture.
 */
// tag::async[]
@Service
public class PriceClient {

    private static final Map<String, BigDecimal> PRICES = Map.of(
            "9780134685991", new BigDecimal("89.90"), "9781617297571", new BigDecimal("95.00"),
            "9780321336781", new BigDecimal("55.00"), "9781449373320", new BigDecimal("110.00"),
            "9781492078005", new BigDecimal("75.00"));

    private final Set<String> threadsSeen = ConcurrentHashMap.newKeySet();

    @Async                                                          // runs on the task executor, not on the caller
    public CompletableFuture<BigDecimal> priceOf(String isbn) {
        Thread current = Thread.currentThread();
        threadsSeen.add((current.isVirtual() ? "virtual:" : "platform:") + current.getName());
        sleep(200);                                                 // the remote call
        BigDecimal price = PRICES.get(isbn);
        return price == null
                ? CompletableFuture.failedFuture(new NoSuchElementException("No price for " + isbn))
                : CompletableFuture.completedFuture(price);
    }
    // end::async[]

    public Set<String> threadsSeen() {
        return Set.copyOf(threadsSeen);
    }

    private static void sleep(long millis) {
        try {
            Thread.sleep(millis);                                   // cheap on a virtual thread
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
