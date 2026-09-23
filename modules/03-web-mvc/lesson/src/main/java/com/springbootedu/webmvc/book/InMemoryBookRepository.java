package com.springbootedu.webmvc.book;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.stereotype.Repository;

/**
 * A thread-safe in-memory store with five sample books.
 */
@Repository
public class InMemoryBookRepository implements BookRepository {

    private final Map<Long, Book> books = new ConcurrentSkipListMap<>();
    private final AtomicLong ids = new AtomicLong();

    public InMemoryBookRepository() {
        save(book("9780134685991", "Effective Java", List.of("Joshua Bloch"), "89.90", "2018-01-06"));
        save(book("9780321336781", "Java Puzzlers", List.of("Joshua Bloch", "Neal Gafter"), "55.00", "2005-07-04"));
        save(book("9781617293566", "Modern Java in Action", List.of("Raoul-Gabriel Urma", "Mario Fusco", "Alan Mycroft"), "120.00", "2018-11-01"));
        save(book("9781617297571", "Spring in Action", List.of("Craig Walls"), "95.00", "2022-03-01"));
        save(book("9780134757599", "Refactoring", List.of("Martin Fowler"), "85.00", "2018-11-20"));
    }

    private static Book book(String isbn, String title, List<String> authors, String price, String publishedOn) {
        return new Book(0, new Isbn(isbn), title, authors, new BigDecimal(price), LocalDate.parse(publishedOn));
    }

    @Override
    public List<Book> findAll() {
        return List.copyOf(books.values());
    }

    @Override
    public Optional<Book> findById(long id) {
        return Optional.ofNullable(books.get(id));
    }

    @Override
    public Book save(Book book) {
        Book stored = book.id() == 0 ? book.withId(ids.incrementAndGet()) : book;
        books.put(stored.id(), stored);
        return stored;
    }

    @Override
    public boolean deleteById(long id) {
        return books.remove(id) != null;
    }
}
