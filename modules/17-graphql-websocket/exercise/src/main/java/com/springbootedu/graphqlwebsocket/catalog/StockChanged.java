package com.springbootedu.graphqlwebsocket.catalog;

/**
 * A Spring application event, published by {@link Inventory} after every change.
 */
public record StockChanged(String isbn, int remaining) {
}
