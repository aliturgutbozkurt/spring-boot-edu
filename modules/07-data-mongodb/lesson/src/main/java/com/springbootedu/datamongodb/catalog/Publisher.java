package com.springbootedu.datamongodb.catalog;

import org.jspecify.annotations.Nullable;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Lesson 3.1 — a separate collection: shared by many books, changes independently.
 */
@Document("publishers")
public record Publisher(@Id @Nullable String id, String name, String country) {

    public Publisher withId(String newId) {                // lets Spring Data set the generated ObjectId
        return new Publisher(newId, name, country);
    }
}
