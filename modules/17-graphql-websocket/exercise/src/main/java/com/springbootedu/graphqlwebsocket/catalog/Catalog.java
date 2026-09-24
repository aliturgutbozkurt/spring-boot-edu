package com.springbootedu.graphqlwebsocket.catalog;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import org.springframework.stereotype.Component;

/**
 * An in-memory catalog. The counter shows how often the books of authors are loaded (N+1 or not).
 */
@Component
public class Catalog {

    private final List<Author> authors = List.of(
            new Author(1, "Joshua Bloch"),
            new Author(2, "Craig Walls"),
            new Author(3, "Martin Kleppmann"));

    private final List<Book> books = List.of(
            new Book("9780134685991", "Effective Java", new BigDecimal("89.90"), 1),
            new Book("9780321336781", "Java Puzzlers", new BigDecimal("55.00"), 1),
            new Book("9781617297571", "Spring in Action", new BigDecimal("95.00"), 2),
            new Book("9781449373320", "Designing Data-Intensive Applications", new BigDecimal("110.00"), 3));

    private final AtomicInteger bookQueries = new AtomicInteger();

    public List<Author> findAllAuthors() {
        return authors;
    }

    public Optional<Author> findAuthor(long id) {
        return authors.stream().filter(author -> author.id() == id).findFirst();
    }

    public Optional<Book> findBook(String isbn) {
        return books.stream().filter(book -> book.isbn().equals(isbn)).findFirst();
    }

    /** The books of ONE author: one "query" per call. */
    public List<Book> findBooksOf(long authorId) {
        bookQueries.incrementAndGet();
        return books.stream().filter(book -> book.authorId() == authorId).toList();
    }

    /** The books of MANY authors in one "query". */
    public List<Book> findBooksOf(Collection<Long> authorIds) {
        bookQueries.incrementAndGet();
        return books.stream().filter(book -> authorIds.contains(book.authorId())).toList();
    }

    public void resetCounters() {
        bookQueries.set(0);
    }

    public int bookQueries() {
        return bookQueries.get();
    }
}
