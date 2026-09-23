package com.springbootedu.datajpapostgres.catalog;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import org.jspecify.annotations.Nullable;

/**
 * Lesson 3.1 — the "one" side of author 1—N book.
 */
// tag::author[]
@Entity
public class Author {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)     // bigserial in PostgreSQL
    private @Nullable Long id;

    private String name;

    @OneToMany(mappedBy = "author")                          // Book.author owns the foreign key
    private List<Book> books = new ArrayList<>();            // collections are LAZY by default

    protected Author() {                                     // required by JPA
        this.name = "";
    }
    // end::author[]

    public Author(String name) {
        this.name = name;
    }

    public @Nullable Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<Book> getBooks() {
        return books;
    }
}
