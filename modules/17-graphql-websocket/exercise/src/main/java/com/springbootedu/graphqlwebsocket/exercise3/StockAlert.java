package com.springbootedu.graphqlwebsocket.exercise3;

/**
 * Sent to /topic/stock-alerts when only a few copies are left.
 */
public record StockAlert(String isbn, int remaining) {
}
