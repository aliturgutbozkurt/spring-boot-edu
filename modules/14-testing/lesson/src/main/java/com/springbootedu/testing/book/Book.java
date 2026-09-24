package com.springbootedu.testing.book;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.math.BigDecimal;
import org.jspecify.annotations.Nullable;

/**
 * Lesson 3.4 — a book with its stock.
 */
@Entity
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private @Nullable Long id;

    private String isbn;
    private String title;
    private BigDecimal price;
    private int stock;

    protected Book() {                                  // for JPA
        this.isbn = "";
        this.title = "";
        this.price = BigDecimal.ZERO;
    }

    public Book(String isbn, String title, BigDecimal price, int stock) {
        this.isbn = isbn;
        this.title = title;
        this.price = price;
        this.stock = stock;
    }

    public void removeFromStock(int quantity) {
        if (quantity > stock) {
            throw new OutOfStockException(isbn, quantity, stock);
        }
        stock -= quantity;
    }

    public @Nullable Long getId() {
        return id;
    }

    public String getIsbn() {
        return isbn;
    }

    public String getTitle() {
        return title;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public int getStock() {
        return stock;
    }
}
