package com.springbootedu.webmvc.book;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Lesson 3.1 — API version 1 of a book: one "author" string and a plain price.
 */
public record BookResponse(long id, Isbn isbn, String title, String author, BigDecimal price, LocalDate publishedOn) {

    static BookResponse from(Book book) {
        return new BookResponse(book.id(), book.isbn(), book.title(), String.join(", ", book.authors()),
                book.price(), book.publishedOn());
    }
}
