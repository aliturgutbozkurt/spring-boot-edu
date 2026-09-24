package com.springbootedu.modulith.catalog;

import com.springbootedu.modulith.catalog.internal.BookRepository;
import java.util.Optional;
import org.springframework.stereotype.Service;

/**
 * The API of the catalog module: other modules use this class, never the repository.
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

    public boolean exists(String isbn) {
        return books.findByIsbn(isbn).isPresent();
    }
}
