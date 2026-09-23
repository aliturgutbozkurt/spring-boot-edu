package com.springbootedu.setupmodernjava.patterns;

import com.springbootedu.setupmodernjava.records.Money;

/**
 * Lesson 3.3 — record patterns take nested records apart in one step; "when" adds a guard.
 */
public final class OrderLinePricing {

    private OrderLinePricing() {
    }

    // tag::record-patterns[]
    public static Money total(OrderLine line) {
        return switch (line) {
            case OrderLine(_, int quantity) when quantity == 0 -> Money.ZERO;
            case OrderLine(Book(_, Money price, Format format), int quantity) when format == Format.EBOOK ->
                    price.times(Math.min(quantity, 3));                  // e-books: pay for at most 3 copies
            case OrderLine(Book(_, Money price, _), int quantity) when quantity >= 10 ->
                    price.times(quantity).percentOff(20);                // bulk order: 20% off
            case OrderLine(Book(_, Money price, _), int quantity) -> price.times(quantity);
        };
    }
    // end::record-patterns[]
}
