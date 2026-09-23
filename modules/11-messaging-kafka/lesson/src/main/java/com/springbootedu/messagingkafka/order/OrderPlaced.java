package com.springbootedu.messagingkafka.order;

/**
 * Lesson 3.1 — the event: something that happened, named in the past tense. Sent as JSON.
 */
public record OrderPlaced(String orderId, String customerId, String isbn, int quantity) {
}
