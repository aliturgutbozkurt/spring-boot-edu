package com.springbootedu.datajpapostgres.exercise3;

import com.springbootedu.datajpapostgres.exercise1.OrderStatus;

/**
 * Exercise 3 — given: one search result.
 */
public record OrderSummary(long id, String customerEmail, OrderStatus status) {
}
