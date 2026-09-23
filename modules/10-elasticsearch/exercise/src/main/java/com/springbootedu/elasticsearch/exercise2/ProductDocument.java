package com.springbootedu.elasticsearch.exercise2;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

/**
 * Given: a product of the shop.
 */
@Document(indexName = "products")
public record ProductDocument(
        @Id String id,
        @Field(type = FieldType.Text) String name,
        @Field(type = FieldType.Keyword) String category,
        @Field(type = FieldType.Double) double price) {
}
