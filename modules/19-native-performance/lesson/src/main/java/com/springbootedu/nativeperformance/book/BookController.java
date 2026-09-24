package com.springbootedu.nativeperformance.book;

import com.springbootedu.nativeperformance.price.PriceFormats;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

/**
 * Lesson 3.1 — the REST API that is measured as JVM, JVM + AOT cache and native image.
 */
@RestController
@RequestMapping("/api/books")
class BookController {

    private final BookRepository books;
    private final PriceFormats priceFormats;

    BookController(BookRepository books, PriceFormats priceFormats) {
        this.books = books;
        this.priceFormats = priceFormats;
    }

    @GetMapping
    List<Book> search(@RequestParam(defaultValue = "") String title) {
        return books.findByTitleContainingIgnoreCaseOrderByTitle(title);
    }

    @GetMapping("/{isbn}")
    Book find(@PathVariable String isbn) {
        return books.findById(isbn).orElseThrow(() -> notFound(isbn));
    }

    @GetMapping("/{isbn}/price")
    String price(@PathVariable String isbn) {
        return priceFormats.format(find(isbn).price());
    }

    @GetMapping("/since/{year}")
    List<Book> since(@PathVariable int year) {
        return books.publishedSince(year);
    }

    private static ResponseStatusException notFound(String isbn) {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, "No book with ISBN " + isbn);   // → ProblemDetail
    }
}
