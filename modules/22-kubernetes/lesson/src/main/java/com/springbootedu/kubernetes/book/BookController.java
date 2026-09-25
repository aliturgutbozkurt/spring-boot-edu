package com.springbootedu.kubernetes.book;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

/**
 * Lesson 3.1 — the application we run on Kubernetes: a small API on PostgreSQL.
 */
@RestController
@RequestMapping("/api/books")
class BookController {

    private final JdbcClient jdbc;

    BookController(JdbcClient jdbc) {
        this.jdbc = jdbc;
    }

    @GetMapping
    List<Book> all() {
        return jdbc.sql("SELECT isbn, title, price FROM book ORDER BY title").query(Book.class).list();
    }

    @GetMapping("/{isbn}")
    Book find(@PathVariable String isbn) {
        return jdbc.sql("SELECT isbn, title, price FROM book WHERE isbn = ?").param(isbn).query(Book.class).optional()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No book with ISBN " + isbn));
    }
}
