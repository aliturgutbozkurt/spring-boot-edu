package com.springbootedu.setupmodernjava.records;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

/**
 * Lesson 3.1 — a record is an immutable data carrier: fields, constructor, accessors,
 * equals/hashCode and toString are generated.
 */
// tag::money[]
public record Money(BigDecimal amount) {

    public static final Money ZERO = new Money(BigDecimal.ZERO);

    public Money {                                        // compact constructor: validate & normalise
        Objects.requireNonNull(amount, "amount");
        if (amount.signum() < 0) {
            throw new IllegalArgumentException("Amount must not be negative: " + amount);
        }
        amount = amount.setScale(2, RoundingMode.HALF_UP);   // assigns the field after this block
    }

    public static Money of(String amount) {
        return new Money(new BigDecimal(amount));
    }

    public Money plus(Money other) {
        return new Money(amount.add(other.amount));      // records are immutable → return a new value
    }
    // end::money[]

    public Money times(int quantity) {
        return new Money(amount.multiply(BigDecimal.valueOf(quantity)));
    }

    public Money percentOff(int percent) {
        return new Money(amount.multiply(BigDecimal.valueOf(100 - percent)).movePointLeft(2));
    }

    public Money minusOrZero(Money other) {
        return new Money(amount.subtract(other.amount).max(BigDecimal.ZERO));
    }

    @Override
    public String toString() {
        return amount.toPlainString();
    }
}
