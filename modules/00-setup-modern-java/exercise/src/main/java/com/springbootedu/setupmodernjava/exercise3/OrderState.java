package com.springbootedu.setupmodernjava.exercise3;

/**
 * Exercise 3 — given: every state an order can be in.
 */
public sealed interface OrderState permits New, Paid, Shipped, Delivered, Cancelled {
}
