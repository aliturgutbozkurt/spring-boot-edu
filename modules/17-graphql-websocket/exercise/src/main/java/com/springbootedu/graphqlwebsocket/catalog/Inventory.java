package com.springbootedu.graphqlwebsocket.catalog;

import java.util.HashMap;
import java.util.Map;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

/**
 * An in-memory stock. Every change is published as a {@link StockChanged} event.
 */
@Component
public class Inventory {

    private final ApplicationEventPublisher events;
    private final Map<String, Integer> stock = new HashMap<>(Map.of(
            "9780134685991", 10,
            "9780321336781", 50,
            "9781617297571", 4,
            "9781449373320", 3));

    Inventory(ApplicationEventPublisher events) {
        this.events = events;
    }

    public synchronized int stockOf(String isbn) {
        return stock.getOrDefault(isbn, 0);
    }

    /** Checks all lines first, so a failing order changes nothing. */
    public synchronized void take(Map<String, Integer> quantities) {
        quantities.forEach((isbn, wanted) -> {
            int available = stockOf(isbn);
            if (wanted > available) {
                throw new OutOfStockException(isbn, wanted, available);
            }
        });
        quantities.forEach((isbn, wanted) -> {
            int remaining = stockOf(isbn) - wanted;
            stock.put(isbn, remaining);
            events.publishEvent(new StockChanged(isbn, remaining));
        });
    }
}
