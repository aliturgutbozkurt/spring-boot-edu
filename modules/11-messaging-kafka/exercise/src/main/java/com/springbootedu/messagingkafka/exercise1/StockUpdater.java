package com.springbootedu.messagingkafka.exercise1;

import com.springbootedu.messagingkafka.events.OrderPlaced;
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

    // TODO 1a: listen to TOPIC as consumer group "stock"
    public void onOrderPlaced(OrderPlaced order) {
        // TODO 1b: Kafka may deliver the same event twice — apply every order only once (see Inventory)
        // TODO 1c: lower the stock of the ordered book
        throw new UnsupportedOperationException("TODO 1 — " + order + inventory);
    }
}
