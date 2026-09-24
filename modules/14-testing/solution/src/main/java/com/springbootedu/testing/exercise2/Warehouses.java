package com.springbootedu.testing.exercise2;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Exercise 2 — given: the warehouses that serve a region. The result is a Set: it has no defined order.
 */
public class Warehouses {

    public Set<String> serving(String region) {
        return new HashSet<>(switch (region) {
            case "marmara" -> List.of("istanbul-asia", "istanbul-europe", "bursa", "kocaeli");
            case "aegean" -> List.of("izmir", "manisa");
            default -> List.of("ankara");
        });
    }
}
