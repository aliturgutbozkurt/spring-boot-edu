package com.springbootedu.hazelcast.exercise2;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Exercise 2 — reserve all books of an order or none, safely under concurrency.
 */
public class OrderReservation {

    private final IMap<String, Integer> stock;

    public OrderReservation(HazelcastInstance hazelcast) {
        this.stock = hazelcast.getMap("stock");
    }

    public void setAvailable(String isbn, int quantity) {
        stock.set(isbn, quantity);
    }

    public int available(String isbn) {
        return Objects.requireNonNullElse(stock.get(isbn), 0);
    }

    public boolean reserveAll(Map<String, Integer> items) {
        List<String> isbns = items.keySet().stream().sorted().toList();   // same order for everyone: no deadlock
        List<String> locked = new ArrayList<>();
        try {
            for (String isbn : isbns) {
                stock.lock(isbn);
                locked.add(isbn);
            }
            for (String isbn : isbns) {
                if (available(isbn) < items.get(isbn)) {
                    return false;                                         // nothing changed yet
                }
            }
            for (String isbn : isbns) {
                stock.set(isbn, available(isbn) - items.get(isbn));
            }
            return true;
        } finally {
            for (String isbn : locked.reversed()) {
                stock.unlock(isbn);
            }
        }
    }
}
