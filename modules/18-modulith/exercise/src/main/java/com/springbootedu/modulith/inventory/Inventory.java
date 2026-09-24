package com.springbootedu.modulith.inventory;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Service;

/**
 * Exercise 3 — the stock of every book.
 */
@Service
public class Inventory {

    private final JdbcClient jdbc;

    Inventory(JdbcClient jdbc) {
        this.jdbc = jdbc;
    }

    public int available(String isbn) {
        return jdbc.sql("SELECT available FROM stock WHERE isbn = ?").param(isbn).query(Integer.class).optional().orElse(0);
    }

    // TODO 3c: once nobody outside this module calls it, make reserve package-private again
    public void reserve(String isbn, int quantity) {
        jdbc.sql("UPDATE stock SET available = available - ? WHERE isbn = ?").params(quantity, isbn).update();
    }
}
