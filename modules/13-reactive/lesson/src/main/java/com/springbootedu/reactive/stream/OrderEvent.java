package com.springbootedu.reactive.stream;

/**
 * Lesson 3.6 — an order, as pushed to the connected clients.
 */
public record OrderEvent(String isbn, int quantity) {
}
