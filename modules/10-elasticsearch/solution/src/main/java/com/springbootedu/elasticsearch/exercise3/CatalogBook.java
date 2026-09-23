package com.springbootedu.elasticsearch.exercise3;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

/**
 * Given: a book as it is stored in the catalog index. The index is created by the Reindexer, not at startup.
 */
@Document(indexName = Reindexer.ALIAS, createIndex = false)
public record CatalogBook(
        @Id String id,
        @Field(type = FieldType.Text) String title) {
}
