package com.springbootedu.graphqlwebsocket.exercise3;

import com.springbootedu.graphqlwebsocket.catalog.StockChanged;
import org.springframework.context.event.EventListener;
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

    @EventListener
    void on(StockChanged change) {
        messaging.convertAndSend("/topic/stock/" + change.isbn(), new StockLevel(change.isbn(), change.remaining()));
        if (change.remaining() < LOW_STOCK) {
            messaging.convertAndSend("/topic/stock-alerts", new StockAlert(change.isbn(), change.remaining()));
        }
    }
}
