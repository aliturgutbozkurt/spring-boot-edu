package com.springbootedu.setupmodernjava.exercise3;

/**
 * Exercise 3 — given: everything that can happen to an order.
 */
public sealed interface OrderEvent permits Pay, Ship, Deliver, Cancel {
}
