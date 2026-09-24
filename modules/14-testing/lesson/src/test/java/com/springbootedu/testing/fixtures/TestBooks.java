package com.springbootedu.testing.fixtures;

import com.springbootedu.testing.book.Book;
import java.math.BigDecimal;

/**
 * Lesson 3.9 — test data in one place: a builder with sensible defaults, so each test states only what matters.
 */
// tag::fixture[]
public final class TestBooks {

    private String isbn = "9780000000000";
    private String title = "A Test Book";
    private BigDecimal price = new BigDecimal("10.00");
    private int stock = 5;

    private TestBooks() {
    }

    public static TestBooks aBook() {
        return new TestBooks();
    }

    public TestBooks isbn(String isbn) {
        this.isbn = isbn;
        return this;
    }

    public TestBooks price(String price) {
        this.price = new BigDecimal(price);
        return this;
    }

    public TestBooks stock(int stock) {
        this.stock = stock;
        return this;
    }

    public TestBooks soldOut() {
        return stock(0);
    }

    public Book build() {
        return new Book(isbn, title, price, stock);
    }
}
// end::fixture[]
