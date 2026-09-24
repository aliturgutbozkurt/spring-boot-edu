package com.springbootedu.modulith.inventory;

import java.util.concurrent.atomic.AtomicBoolean;
import org.springframework.stereotype.Component;

/**
 * Lesson 3.4 — stands for an external warehouse system that can be down. The tour and a test switch it off.
 */
@Component
public class Warehouse {

    private final AtomicBoolean online = new AtomicBoolean(true);

    public void goOffline() {
        online.set(false);
    }

    public void goOnline() {
        online.set(true);
    }

    void confirmReservation(String isbn, int quantity) {
        if (!online.get()) {
            throw new IllegalStateException("Warehouse offline: cannot reserve " + quantity + " × " + isbn);
        }
    }
}
