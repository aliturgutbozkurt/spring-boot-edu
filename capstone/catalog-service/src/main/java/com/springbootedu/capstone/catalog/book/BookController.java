package com.springbootedu.capstone.catalog.book;

import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

/**
 * Reading the catalog is public; changing it needs the role ADMIN (see SecurityConfiguration).
 */
@RestController
@RequestMapping("/api/catalog/books")
class BookController {

    private final BookRepository books;

    BookController(BookRepository books) {
        this.books = books;
    }

    @GetMapping
    List<Book> all() {
        return books.findAll();
    }

    @GetMapping("/{isbn}")
    Book one(@PathVariable String isbn) {
        return books.findById(isbn)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No book with ISBN " + isbn));
    }

    @PutMapping("/{isbn}")
    Book save(@PathVariable String isbn, @Valid @RequestBody BookRequest request) {
        Long version = books.findById(isbn).map(Book::version).orElse(null);
        return books.save(new Book(isbn, request.title(), request.authors(), request.description(), request.price(),
                request.stock(), version));
    }
}
