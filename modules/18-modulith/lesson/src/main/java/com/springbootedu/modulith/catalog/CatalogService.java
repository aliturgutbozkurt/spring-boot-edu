package com.springbootedu.modulith.catalog;

import com.springbootedu.modulith.catalog.internal.BookRepository;
import java.util.Optional;
import org.springframework.stereotype.Service;

/**
 * Lesson 3.1 — the entry point of the catalog module. Other modules call this, never the repository.
 */
@Service
public class CatalogService {

    private final BookRepository books;

    CatalogService(BookRepository books) {
        this.books = books;
    }

    public Optional<Book> find(String isbn) {
        return books.findByIsbn(isbn);
    }
}
