package com.springbootedu.security.exercise2;

/**
 * Given: an order and the customer it belongs to.
 */
public record CustomerOrder(long id, String customer, String isbn) {
}
