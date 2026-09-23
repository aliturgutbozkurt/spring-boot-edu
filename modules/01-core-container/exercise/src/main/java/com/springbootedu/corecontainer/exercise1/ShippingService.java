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
        // TODO 1b: return the shipping method names (the map keys), sorted alphabetically
        throw new UnsupportedOperationException("TODO 1b");
    }

    public BigDecimal cost(String method, BigDecimal orderTotal) {
        // TODO 1c: find the calculator for the method and return its cost.
        //          Unknown method → IllegalArgumentException whose message contains
        //          the method name and the list of valid methods.
        throw new UnsupportedOperationException("TODO 1c");
    }
}
