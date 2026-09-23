package com.springbootedu.webmvc.exercise2;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Service;

/**
 * Exercise 2 — given: stock per ISBN.
 */
@Service
public class StockService {

    private final Map<String, Integer> stock = new ConcurrentHashMap<>(Map.of(
            "9780134685991", 5,
            "9780321336781", 1));

    public int take(String isbn, int quantity) {
        Integer available = stock.get(isbn);
        if (available == null) {
            throw new UnknownBookException(isbn);
        }
        if (available < quantity) {
            throw new OutOfStockException(isbn, quantity, available);
        }
        int remaining = available - quantity;
        stock.put(isbn, remaining);
        return remaining;
    }
}
