package com.springbootedu.modulith.inventory;

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

    // TODO 3b: reserve the stock of every placed order (an application module listener for OrderPlaced)
}
