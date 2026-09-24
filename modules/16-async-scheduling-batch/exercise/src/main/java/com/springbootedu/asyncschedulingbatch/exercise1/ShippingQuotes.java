package com.springbootedu.asyncschedulingbatch.exercise1;

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
        // TODO 1a: ask every carrier in CarrierClient.CARRIERS — start all calls before waiting for any
        // TODO 1b: a carrier that fails gives no quote (it must not fail the whole search)
        // TODO 1c: return the quote with the lowest price
        throw new UnsupportedOperationException("TODO 1 — " + isbn + carriers);
    }
}
