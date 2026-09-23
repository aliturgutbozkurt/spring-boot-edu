package com.springbootedu.webmvc.exercise1;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.stereotype.Repository;

/**
 * Exercise 1 — given: in-memory authors.
 */
@Repository
public class AuthorRepository {

    private final Map<Long, Author> authors = new ConcurrentHashMap<>();
    private final AtomicLong ids = new AtomicLong();

    public AuthorRepository() {
        save("Joshua Bloch", "US");
        save("Sabahattin Ali", "TR");
        save("Oğuz Atay", "TR");
    }

    public List<Author> findAll() {
        return authors.values().stream().sorted(Comparator.comparing(Author::name)).toList();
    }

    public Optional<Author> findById(long id) {
        return Optional.ofNullable(authors.get(id));
    }

    public Author save(String name, String country) {
        var author = new Author(ids.incrementAndGet(), name, country);
        authors.put(author.id(), author);
        return author;
    }
}
