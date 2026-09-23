package com.springbootedu.setupmodernjava.structured;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.StructuredTaskScope;

/**
 * Lesson 3.10 — structured concurrency (PREVIEW in Java 27): subtasks live and die with their scope.
 * Compiled only with the "preview" Maven profile.
 */
public class BookDetailsLoader {

    public record BookDetails(String price, List<String> reviews) {
    }

    private final Duration latency;
    private final boolean reviewServiceDown;

    public BookDetailsLoader(Duration latency, boolean reviewServiceDown) {
        this.latency = latency;
        this.reviewServiceDown = reviewServiceDown;
    }

    // tag::structured[]
    public BookDetails load(String isbn) throws InterruptedException, ExecutionException {
        try (var scope = StructuredTaskScope.open()) {                  // default: all must succeed
            var price = scope.fork(() -> fetchPrice(isbn));             // runs in its own virtual thread
            var reviews = scope.fork(() -> fetchReviews(isbn));

            scope.join();   // waits for both; if one fails, the other is cancelled and ExecutionException is thrown

            return new BookDetails(price.get(), reviews.get());
        }
    }
    // end::structured[]

    private String fetchPrice(String isbn) throws InterruptedException {
        Thread.sleep(latency);
        return "89.90";
    }

    private List<String> fetchReviews(String isbn) throws InterruptedException {
        Thread.sleep(latency);
        if (reviewServiceDown) {
            throw new IllegalStateException("review service down");
        }
        return List.of("★★★★★ Harika / Great");
    }
}
