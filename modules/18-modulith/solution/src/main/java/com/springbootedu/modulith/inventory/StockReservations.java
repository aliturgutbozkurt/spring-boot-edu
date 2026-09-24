package com.springbootedu.modulith.inventory;

import com.springbootedu.modulith.order.OrderPlaced;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Component;

/**
 * Exercise 3 — the inventory reacts to OrderPlaced; the order module does not know it.
 */
@Component
class StockReservations {

    private final Inventory inventory;

    StockReservations(Inventory inventory) {
        this.inventory = inventory;
    }

    @ApplicationModuleListener
    void on(OrderPlaced order) {
        inventory.reserve(order.isbn(), order.quantity());
    }
}
