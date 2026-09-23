package com.springbootedu.messagingkafka.exercise1;

import com.springbootedu.messagingkafka.events.OrderPlaced;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * Exercise 1 — lowers the stock for every order event, exactly once per order.
 */
@Component
public class StockUpdater {

    public static final String TOPIC = "ex1-orders";

    private final Inventory inventory;

    public StockUpdater(Inventory inventory) {
        this.inventory = inventory;
    }

    @KafkaListener(topics = TOPIC, groupId = "stock")
    public void onOrderPlaced(OrderPlaced order) {
        if (!inventory.markProcessed(order.orderId())) {
            return;                                                     // a redelivery: already applied
        }
        inventory.remove(order.isbn(), order.quantity());
    }
}
