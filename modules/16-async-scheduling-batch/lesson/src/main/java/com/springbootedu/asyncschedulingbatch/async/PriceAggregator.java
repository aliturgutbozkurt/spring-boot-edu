package com.springbootedu.asyncschedulingbatch.async;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import org.springframework.stereotype.Service;

/**
 * Lesson 3.1 — starts all calls first, then waits for all of them: the waits overlap.
 */
// tag::aggregate[]
@Service
public class PriceAggregator {

    private final PriceClient prices;

    public PriceAggregator(PriceClient prices) {
        this.prices = prices;                                       // another bean: calls go through the proxy
    }

    public BigDecimal totalFor(List<String> isbns) {
        List<CompletableFuture<BigDecimal>> calls = isbns.stream().map(prices::priceOf).toList();   // all started
        return calls.stream()
                .map(CompletableFuture::join)                       // now wait
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
// end::aggregate[]
