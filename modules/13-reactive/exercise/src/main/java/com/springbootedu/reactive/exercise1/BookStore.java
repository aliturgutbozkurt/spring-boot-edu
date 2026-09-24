package com.springbootedu.reactive.exercise1;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Given: a reactive store (in memory, so the exercise needs no database).
 */
@Component
public class BookStore {

    private final Map<String, Book> books = new ConcurrentHashMap<>(Map.of(
            "9780134685991", new Book("9780134685991", "Effective Java"),
            "9781617297571", new Book("9781617297571", "Spring in Action")));

    public Flux<Book> findAll() {
        return Flux.fromIterable(books.values());
    }

    public Mono<Book> find(String isbn) {
        return Mono.justOrEmpty(books.get(isbn));
    }

    public Mono<Book> save(Book book) {
        return Mono.fromSupplier(() -> {
            books.put(book.isbn(), book);
            return book;
        });
    }

    /**
     * Emits true if the book existed and was removed, false otherwise.
     */
    public Mono<Boolean> delete(String isbn) {
        return Mono.fromSupplier(() -> books.remove(isbn) != null);
    }
}
