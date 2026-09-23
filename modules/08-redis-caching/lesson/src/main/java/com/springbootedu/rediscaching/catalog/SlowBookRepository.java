package com.springbootedu.rediscaching.catalog;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Repository;

/**
 * Plays a slow data source (think: a remote service or a heavy query) and counts how often it is asked.
 */
// tag::slow-source[]
@Repository
public class SlowBookRepository {

    private final Map<String, Book> books = new ConcurrentHashMap<>();
    private final AtomicInteger calls = new AtomicInteger();

    public SlowBookRepository() {
        seed();
    }

    public @Nullable Book findByIsbn(String isbn) {
        calls.incrementAndGet();
        try {
            Thread.sleep(300);                             // the reason we want a cache
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return books.get(isbn);
    }
    // end::slow-source[]

    public void save(Book book) {
        books.put(book.isbn(), book);
    }

    public void delete(String isbn) {
        books.remove(isbn);
    }

    public int calls() {
        return calls.get();
    }

    // Not final: @Repository beans are proxied (exception translation), and a final method would run
    // on the empty proxy instance instead of this object.
    public void reset() {
        seed();
    }

    private void seed() {
        books.clear();
        books.put("9780134685991", new Book("9780134685991", "Effective Java", new BigDecimal("89.90")));
        books.put("9780321336781", new Book("9780321336781", "Java Puzzlers", new BigDecimal("55.00")));
        books.put("9781617297571", new Book("9781617297571", "Spring in Action", new BigDecimal("95.00")));
        calls.set(0);
    }
}
