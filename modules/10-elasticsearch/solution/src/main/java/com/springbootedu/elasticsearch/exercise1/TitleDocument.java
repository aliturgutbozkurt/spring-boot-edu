package com.springbootedu.elasticsearch.exercise1;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

/**
 * Exercise 1 — a book title prepared for autocomplete.
 */
@Document(indexName = "titles")
public record TitleDocument(
        @Id String id,
        @Field(type = FieldType.Search_As_You_Type) String title) {
}
