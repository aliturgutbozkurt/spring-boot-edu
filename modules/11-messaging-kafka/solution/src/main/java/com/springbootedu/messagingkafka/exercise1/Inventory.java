package com.springbootedu.messagingkafka.exercise1;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Component;

/**
 * Given: the stock per book, and the ids of the orders that were already applied.
 */
@Component
public class Inventory {

    private final Map<String, Integer> stock = new ConcurrentHashMap<>();
    private final Set<String> processedOrders = ConcurrentHashMap.newKeySet();

    public void receive(String isbn, int quantity) {
        stock.merge(isbn, quantity, Integer::sum);
    }

    public void remove(String isbn, int quantity) {
        stock.merge(isbn, -quantity, Integer::sum);
    }

    public int available(String isbn) {
        return stock.getOrDefault(isbn, 0);
    }

    /**
     * Remembers the order id. Returns false if it was already remembered.
     */
    public boolean markProcessed(String orderId) {
        return processedOrders.add(orderId);
    }

    public boolean processed(String orderId) {
        return processedOrders.contains(orderId);
    }
}
