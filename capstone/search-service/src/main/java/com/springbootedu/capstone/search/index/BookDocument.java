package com.springbootedu.capstone.search.index;

import java.util.List;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.InnerField;
import org.springframework.data.elasticsearch.annotations.MultiField;

/**
 * ADR-4 — a book in the search read model. Everything but {@code sold} comes from BookChanged events of the
 * catalog; {@code sold} is counted from OrderPlaced events of the order service.
 */
@Document(indexName = "books")
public record BookDocument(
        @Id String isbn,
        @MultiField(mainField = @Field(type = FieldType.Text, analyzer = "english"),
                    otherFields = @InnerField(suffix = "sort", type = FieldType.Keyword))
        String title,
        @Field(type = FieldType.Text) List<String> authors,
        @Field(type = FieldType.Text, analyzer = "english") String description,
        @Field(type = FieldType.Double) double price,
        @Field(type = FieldType.Integer) int stock,
        @Field(type = FieldType.Long) long sold) {
}
