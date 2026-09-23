package com.springbootedu.webmvc.book;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * The domain object. It is never serialised directly — the API exposes DTOs (BookResponse, BookResponseV2).
 */
public record Book(long id, Isbn isbn, String title, List<String> authors, BigDecimal price, LocalDate publishedOn) {

    public Book withId(long newId) {
        return new Book(newId, isbn, title, authors, price, publishedOn);
    }
}
