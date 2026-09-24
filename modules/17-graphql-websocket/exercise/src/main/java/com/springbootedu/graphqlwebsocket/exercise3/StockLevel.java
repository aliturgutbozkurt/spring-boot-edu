package com.springbootedu.graphqlwebsocket.exercise3;

/**
 * Sent to /topic/stock/{isbn} after every stock change.
 */
public record StockLevel(String isbn, int remaining) {
}
