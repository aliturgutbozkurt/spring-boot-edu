package com.springbootedu.rediscaching.exercise1;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Component;

/**
 * Given: a slow product source that counts how often it is read.
 */
@Component
public class ProductCatalog {

    private final Map<String, ProductDetail> products = new ConcurrentHashMap<>();
    private final AtomicInteger reads = new AtomicInteger();

    public ProductCatalog() {
        seed();
    }

    public @Nullable ProductDetail find(String id) {
        reads.incrementAndGet();
        try {
            Thread.sleep(200);                                     // pretend to be a slow remote call
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return products.get(id);
    }

    public void changePrice(String id, BigDecimal newPrice) {
        products.computeIfPresent(id, (key, product) -> new ProductDetail(key, product.name(), newPrice));
    }

    public int reads() {
        return reads.get();
    }

    public void reset() {
        seed();
    }

    private void seed() {
        reads.set(0);
        products.clear();
        products.put("p-1", new ProductDetail("p-1", "Notebook", new BigDecimal("49.90")));
        products.put("p-2", new ProductDetail("p-2", "Fountain pen", new BigDecimal("120.00")));
    }
}
