package com.springbootedu.springai.exercise3;

import java.util.Map;
import java.util.Optional;
import org.springframework.stereotype.Component;

/**
 * Given — the orders and their status.
 */
@Component
public class OrderBook {

    private final Map<String, String> statusById = Map.of(
            "A-1001", "SHIPPED — delivery expected tomorrow",
            "A-1002", "PACKING",
            "A-1003", "DELIVERED");

    public Optional<String> statusOf(String orderId) {
        return Optional.ofNullable(statusById.get(orderId));
    }
}
