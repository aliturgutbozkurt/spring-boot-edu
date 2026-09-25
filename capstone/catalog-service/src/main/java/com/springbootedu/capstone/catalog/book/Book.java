package com.springbootedu.capstone.catalog.book;

import java.math.BigDecimal;
import java.util.List;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.mapping.Document;
import org.jspecify.annotations.Nullable;

/**
 * A book of the catalog, with its stock (ADR-4: the catalog owns books and stock).
 */
@Document("books")
public record Book(@Id String isbn, String title, List<String> authors, String description, BigDecimal price,
                   int stock, @Version @Nullable Long version) {

    public Book withStock(int newStock) {
        return new Book(isbn, title, authors, description, price, newStock, version);
    }
}
