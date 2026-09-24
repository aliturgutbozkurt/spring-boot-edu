package com.springbootedu.modulith.catalog.internal;

import com.springbootedu.modulith.catalog.Book;
import java.util.Optional;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

/**
 * Internal to the catalog module.
 */
@Repository
public class BookRepository {

    private final JdbcClient jdbc;

    BookRepository(JdbcClient jdbc) {
        this.jdbc = jdbc;
    }

    public Optional<Book> findByIsbn(String isbn) {
        return jdbc.sql("SELECT isbn, title, price FROM book WHERE isbn = ?").param(isbn).query(Book.class).optional();
    }
}
