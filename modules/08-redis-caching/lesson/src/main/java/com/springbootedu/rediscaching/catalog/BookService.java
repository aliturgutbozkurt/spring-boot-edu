package com.springbootedu.rediscaching.catalog;

import java.math.BigDecimal;
import org.jspecify.annotations.Nullable;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

/**
 * Lessons 3.1 and 3.3 — cache-aside with annotations: read through the cache, keep it in sync on writes.
 */
// tag::annotations[]
@Service
public class BookService {

    private final SlowBookRepository repository;

    public BookService(SlowBookRepository repository) {
        this.repository = repository;
    }

    @Cacheable(cacheNames = "books", sync = true)                      // key = isbn; sync: one loader per key
    public Book find(String isbn) {
        Book book = repository.findByIsbn(isbn);
        if (book == null) {
            throw new BookNotFoundException(isbn);                     // exceptions are not cached
        }
        return book;
    }

    @Cacheable(cacheNames = "books", unless = "#result == null")        // do not cache "not found"
    public @Nullable Book findOrNull(String isbn) {
        return repository.findByIsbn(isbn);
    }

    @CachePut(cacheNames = "books", key = "#isbn")                     // write-through: update source AND cache
    public Book changePrice(String isbn, BigDecimal newPrice) {
        Book updated = new Book(isbn, find(isbn).title(), newPrice);   // find(): self-call, not cached here
        repository.save(updated);
        return updated;
    }

    @CacheEvict(cacheNames = "books")                                  // remove the stale entry
    public void remove(String isbn) {
        repository.delete(isbn);
    }
}
// end::annotations[]
