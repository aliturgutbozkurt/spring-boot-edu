package com.springbootedu.setupmodernjava.sealed;

import com.springbootedu.setupmodernjava.records.Money;

/**
 * Lesson 3.2 — pattern matching for switch over a sealed type: no default branch needed.
 */
public final class Discounts {

    private Discounts() {
    }

    // tag::exhaustive-switch[]
    public static Money apply(Discount discount, Money price) {
        return switch (discount) {                        // exhaustive: every permitted type is covered
            case PercentageDiscount p -> price.percentOff(p.percent());
            case FixedDiscount f -> price.minusOrZero(f.amount());
            case NoDiscount _ -> price;                   // "_" = unnamed variable (we don't need it)
        };
        // Add a fourth Discount type and this method stops compiling until you handle it.
    }
    // end::exhaustive-switch[]

    public static String describe(Discount discount) {
        return switch (discount) {
            case PercentageDiscount(int percent) -> "%" + percent + " indirim / " + percent + "% off";
            case FixedDiscount(Money amount) -> amount + " TL indirim / " + amount + " TRY off";
            case NoDiscount() -> "İndirim yok / No discount";
        };
    }
}
