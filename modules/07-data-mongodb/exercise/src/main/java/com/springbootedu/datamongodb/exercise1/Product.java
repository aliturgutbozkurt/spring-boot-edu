package com.springbootedu.datamongodb.exercise1;

import java.math.BigDecimal;
import java.util.Map;
import org.jspecify.annotations.Nullable;
import org.springframework.data.annotation.Id;

/**
 * Exercise 1 — one collection for all products; each kind has its own attributes.
 */
// TODO 1a: store products in the collection "products"
public record Product(@Id @Nullable String id,
                      String sku,                                  // TODO 1a: no two products may share a SKU
                      String name, String category, BigDecimal price, Map<String, Object> attributes) {

    public Product withId(String newId) {
        return new Product(newId, sku, name, category, price, attributes);
    }
}
