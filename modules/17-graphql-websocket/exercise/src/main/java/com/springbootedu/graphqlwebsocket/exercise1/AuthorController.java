package com.springbootedu.graphqlwebsocket.exercise1;

import com.springbootedu.graphqlwebsocket.catalog.Catalog;
import org.springframework.stereotype.Controller;

/**
 * Exercise 1 — Query.authors, Query.author and Author.books.
 */
@Controller
class AuthorController {

    private final Catalog catalog;

    AuthorController(Catalog catalog) {
        this.catalog = catalog;
    }

    // TODO 1b: Query.authors (all authors) and Query.author(id) (null when unknown)

    // TODO 1c: Author.books — the books of ALL authors of the result with ONE catalog call
}
