package com.springbootedu.setupmodernjava.patterns;

import static org.assertj.core.api.Assertions.assertThat;

import com.springbootedu.setupmodernjava.records.Money;
import org.junit.jupiter.api.Test;

/**
 * Lesson 3.3 — record patterns (deconstruction) with guards.
 */
class OrderLinePricingTest {

    private static final Book JAVA = new Book("Effective Java", Money.of("100"), Format.PAPERBACK);
    private static final Book EBOOK = new Book("Java Puzzlers", Money.of("40"), Format.EBOOK);

    @Test
    void regularLine() {
        assertThat(OrderLinePricing.total(new OrderLine(JAVA, 2))).isEqualTo(Money.of("200.00"));
    }

    @Test
    void bulkOrderOfPrintedBooksGets20PercentOff() {
        assertThat(OrderLinePricing.total(new OrderLine(JAVA, 10))).isEqualTo(Money.of("800.00"));
    }

    @Test
    void ebooksAreNeverDiscountedButCostAtMostThreeCopies() {
        assertThat(OrderLinePricing.total(new OrderLine(EBOOK, 10))).isEqualTo(Money.of("120.00"));
    }

    @Test
    void emptyLineIsFree() {
        assertThat(OrderLinePricing.total(new OrderLine(JAVA, 0))).isEqualTo(Money.of("0.00"));
    }
}
