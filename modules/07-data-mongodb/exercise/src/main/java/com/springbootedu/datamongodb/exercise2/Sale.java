package com.springbootedu.datamongodb.exercise2;

import java.math.BigDecimal;
import java.time.Instant;
import org.jspecify.annotations.Nullable;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Exercise 2 — given: one sale.
 */
@Document("sales")
public record Sale(@Id @Nullable String id, String category, int quantity, BigDecimal unitPrice, Instant soldAt) {

    public Sale withId(String newId) {
        return new Sale(newId, category, quantity, unitPrice, soldAt);
    }
}
