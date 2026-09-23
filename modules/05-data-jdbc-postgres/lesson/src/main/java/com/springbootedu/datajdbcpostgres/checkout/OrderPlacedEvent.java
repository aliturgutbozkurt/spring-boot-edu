package com.springbootedu.datajdbcpostgres.checkout;

/**
 * Lesson 3.6 — published inside the checkout transaction, handled after it commits.
 */
public record OrderPlacedEvent(long orderId, String customerEmail) {
}
