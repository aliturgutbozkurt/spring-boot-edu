package com.springbootedu.setupmodernjava.exercise2;

/**
 * Exercise 2 — given: a blocking remote call.
 */
public interface StockClient {

    int stockOf(String isbn) throws InterruptedException;
}
