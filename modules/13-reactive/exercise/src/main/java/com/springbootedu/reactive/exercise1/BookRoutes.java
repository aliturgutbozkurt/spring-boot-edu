package com.springbootedu.reactive.exercise1;

import org.springframework.context.annotation.Configuration;

/**
 * Exercise 1 — the book API as functional endpoints.
 */
@Configuration(proxyBeanMethods = false)
public class BookRoutes {

    // TODO 1a: a RouterFunction<ServerResponse> bean with the prefix /api/books (use BookStore)
    // TODO 1b: GET  ""        → 200 with all books
    //          GET  "/{isbn}" → 200 with the book, or 404 if the store returns an empty Mono
    // TODO 1c: POST ""        → save the book from the body, answer 201 with Location /api/books/{isbn}
    // TODO 1d: DELETE "/{isbn}" → 204 if the book was deleted, 404 if it did not exist
}
