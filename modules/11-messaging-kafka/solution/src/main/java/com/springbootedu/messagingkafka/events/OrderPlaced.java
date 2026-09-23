package com.springbootedu.messagingkafka.events;

/**
 * Given: an order was placed.
 */
public record OrderPlaced(String orderId, String isbn, int quantity) {
}
