package com.springbootedu.httpclientsresilience.exercise1;

import java.util.List;

/**
 * Exercise 1 — the review service as an HTTP interface.
 */
// TODO 1a: all methods live under the path "/reviews"
public interface ReviewApi {

    // TODO 1b: GET /reviews/{isbn} → the reviews of a book
    List<Review> forBook(String isbn);

    // TODO 1c: POST /reviews/{isbn} with the review as the JSON body
    void add(String isbn, Review review);
}
