package com.springbootedu.asyncschedulingbatch.exercise1;

import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import org.springframework.stereotype.Service;

/**
 * Exercise 1 — the cheapest carrier for a book.
 */
@Service
public class ShippingQuotes {

    private final CarrierClient carriers;

    public ShippingQuotes(CarrierClient carriers) {
        this.carriers = carriers;
    }

    public Quote cheapest(String isbn) {
        List<CompletableFuture<Quote>> calls = CarrierClient.CARRIERS.stream()
                .map(carrier -> carriers.quote(carrier, isbn)
                        .exceptionally(error -> null))                    // a failing carrier gives no quote
                .toList();
        return calls.stream()
                .map(CompletableFuture::join)
                .filter(Objects::nonNull)
                .min(Comparator.comparing(Quote::price))
                .orElseThrow(() -> new NoSuchElementException("No carrier can deliver " + isbn));
    }
}
