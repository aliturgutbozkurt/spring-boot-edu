package com.springbootedu.hazelcast.catalog;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;
import com.hazelcast.query.Predicates;
import java.math.BigDecimal;
import java.time.Duration;
import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import org.springframework.stereotype.Service;

/**
 * Lessons 3.2–3.3 — an IMap behaves like a {@code ConcurrentMap}, but its entries are spread over the cluster.
 */
// tag::imap[]
@Service
public class BookCatalog {

    private final IMap<String, Book> books;

    public BookCatalog(HazelcastInstance hazelcast) {
        this.books = hazelcast.getMap("books");                       // created on first use
    }

    public void save(Book book) {
        books.set(book.isbn(), book);                                 // set(): like put() without returning the old value
    }

    public boolean addIfAbsent(Book book) {
        return books.putIfAbsent(book.isbn(), book) == null;          // atomic across the whole cluster
    }

    public void saveFor(Book book, Duration timeToLive) {
        books.set(book.isbn(), book, timeToLive.toMillis(), TimeUnit.MILLISECONDS);   // this entry expires on its own
    }

    public Optional<Book> find(String isbn) {
        return Optional.ofNullable(books.get(isbn));
    }
    // end::imap[]

    // tag::query[]
    public Collection<Book> cheaperThan(BigDecimal limit) {
        return books.values(Predicates.lessThan("price", limit));    // runs on every member, in parallel
    }
    // end::query[]

    public void clear() {
        books.clear();
    }
}
