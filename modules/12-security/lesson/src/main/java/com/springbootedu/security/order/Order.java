package com.springbootedu.security.order;

/**
 * Lesson 3.4 — an order belongs to the customer who placed it.
 */
public record Order(long id, String customer, String isbn, int quantity) {
}
