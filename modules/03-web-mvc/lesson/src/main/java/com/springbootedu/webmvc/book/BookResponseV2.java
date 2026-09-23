package com.springbootedu.webmvc.book;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Lesson 3.5 — API version 2: an author list and a price with currency. A breaking change → new version.
 */
// tag::v2[]
public record BookResponseV2(long id, Isbn isbn, String title, List<String> authors, Price price, LocalDate publishedOn) {

    public record Price(BigDecimal amount, String currency) {
    }
    // end::v2[]

    static BookResponseV2 from(Book book) {
        return new BookResponseV2(book.id(), book.isbn(), book.title(), book.authors(),
                new Price(book.price(), "TRY"), book.publishedOn());
    }
}
