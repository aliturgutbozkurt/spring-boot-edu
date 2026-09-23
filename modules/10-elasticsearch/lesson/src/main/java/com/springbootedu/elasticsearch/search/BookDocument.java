package com.springbootedu.elasticsearch.search;

import com.springbootedu.elasticsearch.catalog.Book;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.InnerField;
import org.springframework.data.elasticsearch.annotations.MultiField;

/**
 * Lesson 3.1 — how a book is stored in the "books" index. The mapping decides what can be searched and how.
 */
// tag::document[]
@Document(indexName = "books")
public record BookDocument(
        @Id String id,
        @MultiField(mainField = @Field(type = FieldType.Text),                        // searchable words …
                    otherFields = @InnerField(suffix = "sort", type = FieldType.Keyword))  // … and sortable as a whole
        String title,
        @Field(type = FieldType.Keyword) String author,                                 // exact values only
        @Field(type = FieldType.Text, analyzer = "turkish") String description,         // Turkish stemming + lowercase
        @Field(type = FieldType.Keyword) String category,                               // filters and facets
        @Field(type = FieldType.Double) double price) {

    public static BookDocument from(Book book) {
        return new BookDocument(String.valueOf(book.id()), book.title(), book.author(), book.description(),
                book.category(), book.price().doubleValue());
    }
}
// end::document[]
