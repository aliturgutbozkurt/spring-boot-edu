package com.springbootedu.corecontainer.book;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.springframework.stereotype.Repository;

/**
 * Lesson 3.1 — a {@code @Repository} is a {@code @Component}: component scanning registers it as a bean.
 */
@Repository
public class InMemoryBookCatalog implements BookCatalog {

    private final List<Book> books = new CopyOnWriteArrayList<>(List.of(
            new Book("978-0-13-468599-1", "Effective Java", "Joshua Bloch", new BigDecimal("89.90")),
            new Book("978-0-321-33678-1", "Java Puzzlers", "Joshua Bloch", new BigDecimal("55.00")),
            new Book("978-1-61729-356-6", "Modern Java in Action", "Raoul-Gabriel Urma", new BigDecimal("120.00")),
            new Book("978-1-61729-757-1", "Spring in Action", "Craig Walls", new BigDecimal("95.00"))));

    @Override
    public List<Book> findAll() {
        return List.copyOf(books);
    }

    @Override
    public void save(Book book) {
        books.add(book);
    }
}
