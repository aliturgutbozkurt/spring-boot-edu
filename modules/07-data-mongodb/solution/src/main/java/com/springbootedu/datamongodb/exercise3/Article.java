package com.springbootedu.datamongodb.exercise3;

import org.jspecify.annotations.Nullable;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.TextIndexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Exercise 3 — blog articles with a weighted text index.
 */
@Document("articles")
public record Article(@Id @Nullable String id,
                      @TextIndexed(weight = 3) String title,
                      @TextIndexed String body) {

    public Article withId(String newId) {
        return new Article(newId, title, body);
    }
}
