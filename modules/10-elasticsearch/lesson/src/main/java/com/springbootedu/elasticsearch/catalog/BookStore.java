package com.springbootedu.elasticsearch.catalog;

import java.util.List;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Lesson 3.6 — writes books to PostgreSQL and announces every write with an event.
 */
// tag::publish[]
@Service
public class BookStore {

    private final JdbcClient jdbc;
    private final ApplicationEventPublisher events;

    public BookStore(JdbcClient jdbc, ApplicationEventPublisher events) {
        this.jdbc = jdbc;
        this.events = events;
    }

    @Transactional
    public Book add(NewBook book) {
        Book saved = insert(book);
        events.publishEvent(new BookSaved(saved));                  // delivered after the commit (BookIndexer)
        return saved;
    }
    // end::publish[]

    @Transactional
    public List<Book> addAll(List<NewBook> books) {
        return books.stream().map(this::add).toList();              // self-calls: all in this one transaction
    }

    public List<Book> findAll() {
        return jdbc.sql("SELECT * FROM book ORDER BY id").query(Book.class).list();
    }

    private Book insert(NewBook book) {
        long id = jdbc.sql("""
                        INSERT INTO book (isbn, title, author, description, category, price)
                        VALUES (:isbn, :title, :author, :description, :category, :price)
                        RETURNING id""")
                .paramSource(book)
                .query(Long.class)
                .single();
        return new Book(id, book.isbn(), book.title(), book.author(), book.description(), book.category(),
                book.price());
    }
}
