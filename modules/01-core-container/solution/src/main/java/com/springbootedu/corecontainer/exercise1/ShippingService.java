package com.springbootedu.corecontainer.exercise1;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;

/**
 * Exercise 1 — Spring injects every {@link ShippingCalculator} bean into the map, keyed by bean name.
 */
@Service
public class ShippingService {

    private final Map<String, ShippingCalculator> calculators;

    public ShippingService(Map<String, ShippingCalculator> calculators) {
        this.calculators = calculators;
    }

    public List<String> availableMethods() {
        return calculators.keySet().stream().sorted().toList();
    }

    public BigDecimal cost(String method, BigDecimal orderTotal) {
        ShippingCalculator calculator = calculators.get(method);
        if (calculator == null) {
            throw new IllegalArgumentException("Unknown shipping method '" + method + "'. Valid methods: " + availableMethods());
        }
        return calculator.cost(orderTotal);
    }
}
