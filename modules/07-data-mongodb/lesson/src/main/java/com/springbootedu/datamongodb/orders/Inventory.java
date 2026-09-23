package com.springbootedu.datamongodb.orders;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Lesson 3.6 — stock per ISBN, in a separate collection.
 */
@Document("inventory")
public record Inventory(@Id String isbn, int quantity) {
}
