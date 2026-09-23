package com.springbootedu.datamongodb.orders;

import java.time.Instant;
import org.jspecify.annotations.Nullable;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Lesson 3.6 — an order document.
 */
@Document("orders")
public record Order(@Id @Nullable String id, String customer, String isbn, int quantity, Instant createdAt) {

    public Order withId(String newId) {
        return new Order(newId, customer, isbn, quantity, createdAt);
    }
}
