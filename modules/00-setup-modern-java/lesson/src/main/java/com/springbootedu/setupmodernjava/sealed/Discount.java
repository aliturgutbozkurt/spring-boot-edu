package com.springbootedu.setupmodernjava.sealed;

/**
 * Lesson 3.2 — a sealed interface lists all of its implementations; the compiler knows there are no others.
 */
// tag::sealed[]
public sealed interface Discount permits PercentageDiscount, FixedDiscount, NoDiscount {
}
// end::sealed[]
