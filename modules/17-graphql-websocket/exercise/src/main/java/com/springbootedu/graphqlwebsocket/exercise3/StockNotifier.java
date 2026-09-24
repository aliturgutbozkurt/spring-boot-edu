package com.springbootedu.graphqlwebsocket.exercise3;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

/**
 * Exercise 3 — turns inventory events into STOMP messages.
 */
@Component
class StockNotifier {

    static final int LOW_STOCK = 3;                   // fewer copies than this → an alert

    private final SimpMessagingTemplate messaging;

    StockNotifier(SimpMessagingTemplate messaging) {
        this.messaging = messaging;
    }

    // TODO 3a: on every StockChanged event of the Inventory, send a StockLevel to /topic/stock/{isbn}
    // TODO 3b: when fewer than LOW_STOCK copies are left, also send a StockAlert to /topic/stock-alerts
}
