package com.springbootedu.modulith.inventory;

import com.springbootedu.modulith.order.OrderPlaced;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Component;

/**
 * Lesson 3.3 — the inventory module reacts to an event of the order module.
 */
@Component
class StockReservations {

    private final Inventory inventory;
    private final Warehouse warehouse;

    StockReservations(Inventory inventory, Warehouse warehouse) {
        this.inventory = inventory;
        this.warehouse = warehouse;
    }

    // tag::listener[]
    @ApplicationModuleListener        // = @TransactionalEventListener + @Async + @Transactional(REQUIRES_NEW)
    void on(OrderPlaced order) {
        inventory.reserve(order.isbn(), order.quantity());
        warehouse.confirmReservation(order.isbn(), order.quantity());   // throws → rollback, publication FAILED
    }
    // end::listener[]
}
