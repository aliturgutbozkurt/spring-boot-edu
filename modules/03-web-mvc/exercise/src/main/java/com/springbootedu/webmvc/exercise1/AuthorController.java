package com.springbootedu.webmvc.exercise1;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Exercise 1 — the author endpoint.
 */
@RestController
@RequestMapping("/api/authors")
public class AuthorController {

    private final AuthorRepository authors;

    public AuthorController(AuthorRepository authors) {
        this.authors = authors;
    }

    // TODO 1a: GET /api/authors        → all authors (the repository already sorts them by name)
    // TODO 1b: GET /api/authors/{id}   → one author, 404 when it does not exist
    // TODO 1c: POST /api/authors       → validated AuthorRequest; 201 Created + Location header + the new author
    //          Use the "authors" repository field for all three.
}
