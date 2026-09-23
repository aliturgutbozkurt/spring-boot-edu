package com.springbootedu.httpclientsresilience.exercise1;

import java.util.List;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;

/**
 * Exercise 1 — the review service as an HTTP interface.
 */
@HttpExchange("/reviews")
public interface ReviewApi {

    @GetExchange("/{isbn}")
    List<Review> forBook(@PathVariable String isbn);

    @PostExchange("/{isbn}")
    void add(@PathVariable String isbn, @RequestBody Review review);
}
