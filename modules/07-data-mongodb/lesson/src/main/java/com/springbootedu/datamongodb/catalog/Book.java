package com.springbootedu.datamongodb.catalog;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import org.jspecify.annotations.Nullable;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.index.TextIndexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

/**
 * Lesson 3.1 — one document per book: embedded reviews, a free-form attribute map, a referenced publisher.
 */
// tag::document[]
@Document("books")
public record Book(
        @Id @Nullable String id,
        @Indexed(unique = true) String isbn,                // Lesson 3.5: a unique index
        @TextIndexed String title,                          // Lesson 3.5: part of the full-text index
        List<String> authors,                               // arrays are first-class in MongoDB
        BigDecimal price,                                   // stored as Decimal128 (see application.yaml)
        int stock,
        List<String> categories,
        Map<String, String> attributes,                     // different books, different attributes — no schema change
        @DocumentReference @Nullable Publisher publisher,   // stores only the publisher's id
        List<Review> reviews) {                             // embedded: stored inside this document
    // end::document[]

    public Book withId(String newId) {
        return new Book(newId, isbn, title, authors, price, stock, categories, attributes, publisher, reviews);
    }
}
