package com.springbootedu.asyncschedulingbatch.exercise1;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * Given: asks one carrier for a price. Each call takes about 300 ms.
 */
@Component
public class CarrierClient {

    public static final List<String> CARRIERS = List.of("yurtici", "mng", "aras");

    private static final Map<String, BigDecimal> PRICES = Map.of(
            "yurtici", new BigDecimal("24.90"), "mng", new BigDecimal("29.90"), "aras", new BigDecimal("34.90"));

    @Async
    public CompletableFuture<Quote> quote(String carrier, String isbn) {
        pause();
        if (carrier.equals("yurtici") && isbn.equals("9781617297571")) {
            return CompletableFuture.failedFuture(new IllegalStateException("yurtici cannot deliver " + isbn));
        }
        return CompletableFuture.completedFuture(new Quote(carrier, PRICES.get(carrier)));
    }

    private static void pause() {
        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
