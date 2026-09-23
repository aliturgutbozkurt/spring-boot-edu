package com.springbootedu.datamongodb.exercise1;

import java.math.BigDecimal;
import java.util.Map;
import org.jspecify.annotations.Nullable;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Exercise 1 — one collection for all products; each kind has its own attributes.
 */
@Document("products")
public record Product(@Id @Nullable String id, @Indexed(unique = true) String sku, String name, String category,
                      BigDecimal price, Map<String, Object> attributes) {

    public Product withId(String newId) {
        return new Product(newId, sku, name, category, price, attributes);
    }
}
