package com.springbootedu.security.book;

import java.net.URI;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Lesson 3.3 — the controller knows nothing about security; the API chain decides who may call what.
 */
@RestController
@RequestMapping("/api/books")
class BookController {

    private final List<Book> books = new CopyOnWriteArrayList<>(List.of(
            new Book("9780134685991", "Effective Java"),
            new Book("9781617297571", "Spring in Action")));

    @GetMapping
    List<Book> list() {
        return List.copyOf(books);
    }

    @PostMapping
    ResponseEntity<Book> add(@RequestBody Book book) {
        books.add(book);
        return ResponseEntity.created(URI.create("/api/books/" + book.isbn())).body(book);
    }
}
