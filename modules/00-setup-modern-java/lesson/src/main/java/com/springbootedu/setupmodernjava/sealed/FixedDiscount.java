package com.springbootedu.setupmodernjava.sealed;

import com.springbootedu.setupmodernjava.records.Money;

/**
 * Lesson 3.2 — a fixed amount off.
 */
public record FixedDiscount(Money amount) implements Discount {
}
