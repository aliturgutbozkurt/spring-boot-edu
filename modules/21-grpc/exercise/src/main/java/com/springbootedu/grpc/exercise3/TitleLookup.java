package com.springbootedu.grpc.exercise3;

import com.springbootedu.grpc.exercises.v1.BookServiceGrpc;
import java.time.Duration;
import java.util.Optional;
import org.springframework.stereotype.Component;

/**
 * Exercise 3 — a client that never waits longer than the caller allows.
 */
@Component
public class TitleLookup {

    private final BookServiceGrpc.BookServiceBlockingStub books;

    TitleLookup(BookServiceGrpc.BookServiceBlockingStub books) {
        this.books = books;
    }

    public Optional<String> titleWithin(String isbn, Duration timeout) {
        // TODO 3a: call GetBook with a deadline of "timeout"
        // TODO 3b: DEADLINE_EXCEEDED → Optional.empty(); every other error is thrown on
        throw new UnsupportedOperationException("TODO 3 — " + books + isbn + timeout);
    }
}
