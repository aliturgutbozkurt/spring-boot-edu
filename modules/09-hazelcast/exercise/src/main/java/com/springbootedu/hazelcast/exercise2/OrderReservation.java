package com.springbootedu.hazelcast.exercise2;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;
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
        // TODO 2a: lock the key of every book in the order — in an order that avoids deadlocks
        // TODO 2b: if any book has less stock than ordered, change nothing and return false
        // TODO 2c: otherwise lower the stock of every book and return true
        // TODO 2d: release every lock you took, also when something goes wrong
        throw new UnsupportedOperationException("TODO 2 — " + items);
    }
}
