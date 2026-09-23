package com.springbootedu.messagingkafka.invoicing;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.springframework.stereotype.Component;

/**
 * Lesson 3.4 — remembers invoiced orders and how many attempts each one needed.
 */
@Component
public class Invoices {

    private final List<String> invoiced = new CopyOnWriteArrayList<>();
    private final Map<String, Integer> attempts = new ConcurrentHashMap<>();

    int countAttempt(String orderId) {
        return attempts.merge(orderId, 1, Integer::sum);
    }

    void invoice(String orderId) {
        invoiced.add(orderId);
    }

    public List<String> invoiced() {
        return List.copyOf(invoiced);
    }

    public int attempts(String orderId) {
        return attempts.getOrDefault(orderId, 0);
    }
}
