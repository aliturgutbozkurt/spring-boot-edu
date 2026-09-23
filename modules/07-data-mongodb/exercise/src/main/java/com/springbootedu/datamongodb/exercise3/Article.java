package com.springbootedu.datamongodb.exercise3;

import org.jspecify.annotations.Nullable;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Exercise 3 — blog articles with a weighted text index.
 */
@Document("articles")
public record Article(@Id @Nullable String id,
                      String title,         // TODO 3a: part of the text index, weight 3
                      String body) {        // TODO 3a: part of the text index, weight 1

    public Article withId(String newId) {
        return new Article(newId, title, body);
    }
}
