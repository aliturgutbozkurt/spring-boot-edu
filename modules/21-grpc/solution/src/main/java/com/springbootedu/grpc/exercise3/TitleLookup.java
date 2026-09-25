package com.springbootedu.grpc.exercise3;

import com.springbootedu.grpc.exercises.v1.BookServiceGrpc;
import com.springbootedu.grpc.exercises.v1.GetBookRequest;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
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
        try {
            String title = books.withDeadlineAfter(timeout.toMillis(), TimeUnit.MILLISECONDS)
                    .getBook(GetBookRequest.newBuilder().setIsbn(isbn).build())
                    .getTitle();
            return Optional.of(title);
        } catch (StatusRuntimeException e) {
            if (e.getStatus().getCode() == Status.Code.DEADLINE_EXCEEDED) {
                return Optional.empty();                // too slow: the caller shows the page without the title
            }
            throw e;
        }
    }
}
