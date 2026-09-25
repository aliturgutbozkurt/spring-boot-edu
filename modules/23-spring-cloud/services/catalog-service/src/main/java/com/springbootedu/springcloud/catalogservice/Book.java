package com.springbootedu.springcloud.catalogservice;

import java.math.BigDecimal;

/**
 * Lesson 3.2 — a book, with the name of the instance that answered (to see the load balancer at work).
 */
public record Book(String isbn, String title, BigDecimal price, String servedBy) {
}
