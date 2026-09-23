package com.springbootedu.datajpapostgres.catalog;

import jakarta.persistence.criteria.Join;
import java.math.BigDecimal;
import java.util.Locale;
import org.springframework.data.jpa.domain.Specification;

/**
 * Lesson 3.5 — small, reusable query building blocks.
 */
// tag::specifications[]
final class BookSpecifications {

    private BookSpecifications() {
    }

    static Specification<Book> titleContains(String text) {
        return (book, query, cb) -> cb.like(cb.lower(book.get("title")), "%" + text.toLowerCase(Locale.ROOT) + "%");
    }

    static Specification<Book> priceAtMost(BigDecimal max) {
        return (book, query, cb) -> cb.lessThanOrEqualTo(book.get("price"), max);
    }

    static Specification<Book> inCategory(String name) {
        return (book, query, cb) -> {
            Join<Book, Category> categories = book.join("categories");
            return cb.equal(categories.get("name"), name);
        };
    }
}
// end::specifications[]
