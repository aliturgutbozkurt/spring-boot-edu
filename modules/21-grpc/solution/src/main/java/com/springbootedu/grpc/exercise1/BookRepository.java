package com.springbootedu.grpc.exercise1;

import com.springbootedu.grpc.exercises.v1.Book;
import java.time.Duration;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Given — the books, and an optional latency for exercise 3 (bookstore.latency).
 */
@Component
public class BookRepository {

    private final Duration latency;
    private final List<Book> books;

    BookRepository(@Value("${bookstore.latency:0ms}") Duration latency) {
        this.latency = latency;
        this.books = List.of(
                book("9780134685991", "Effective Java", 8990),
                book("9780321336781", "Java Puzzlers", 5500),
                book("9781617297571", "Spring in Action", 9500),
                book("9781449373320", "Designing Data-Intensive Applications", 11000));
    }

    public Optional<Book> find(String isbn) {
        sleep();
        return books.stream().filter(book -> book.getIsbn().equals(isbn)).findFirst();
    }

    public List<Book> all() {
        return books;
    }

    private void sleep() {
        try {
            Thread.sleep(latency);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private static Book book(String isbn, String title, long priceCents) {
        return Book.newBuilder().setIsbn(isbn).setTitle(title).setPriceCents(priceCents).build();
    }
}
