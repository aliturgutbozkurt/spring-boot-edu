package com.springbootedu.grpc.catalog;

import com.springbootedu.grpc.catalog.v1.Book;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Lesson 3.2 — an in-memory store; the service works with the generated protobuf messages directly.
 */
@Component
@EnableConfigurationProperties(CatalogProperties.class)
public class BookStore {

    private final CatalogProperties properties;
    private final Map<String, Book> books = new ConcurrentSkipListMap<>();
    private final Map<String, Integer> stock = new ConcurrentHashMap<>();

    BookStore(CatalogProperties properties) {
        this.properties = properties;
        add(book("9780134685991", "Effective Java", "Joshua Bloch", 8990), 12);
        add(book("9780321336781", "Java Puzzlers", "Joshua Bloch", 5500), 5);
        add(book("9781617297571", "Spring in Action", "Craig Walls", 9500), 3);
        add(book("9781449373320", "Designing Data-Intensive Applications", "Martin Kleppmann", 11000), 7);
    }

    public Optional<Book> find(String isbn) {
        simulateLatency();
        return Optional.ofNullable(books.get(isbn));
    }

    public List<Book> byAuthor(String author) {
        return books.values().stream()
                .filter(book -> author.isEmpty() || book.getAuthor().equals(author))
                .toList();
    }

    public void add(Book book, int copies) {
        books.put(book.getIsbn(), book);
        stock.merge(book.getIsbn(), copies, Integer::sum);
    }

    public int stockOf(String isbn) {
        return stock.getOrDefault(isbn, 0);
    }

    private void simulateLatency() {
        try {
            Thread.sleep(properties.latency());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private static Book book(String isbn, String title, String author, long priceCents) {
        return Book.newBuilder().setIsbn(isbn).setTitle(title).setAuthor(author).setPriceCents(priceCents).build();
    }
}
