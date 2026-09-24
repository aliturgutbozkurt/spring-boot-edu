package com.springbootedu.modulith.loyalty;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Service;

/**
 * Exercise 2 — the API of the loyalty module: one point for every full 10 of an order's total.
 */
@Service
public class LoyaltyPoints {

    private final Map<String, Integer> points = new ConcurrentHashMap<>();

    public int pointsOf(String customerId) {
        return points.getOrDefault(customerId, 0);
    }

    void add(String customerId, int newPoints) {
        points.merge(customerId, newPoints, Integer::sum);
    }

    // TODO 2a: a new class in this package listens to OrderPlaced events of the order module ...
    // TODO 2b: ... and adds one point for every full 10 of the order's total (179.80 → 17) with add(...)
}
