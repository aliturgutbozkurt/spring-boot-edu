package com.springbootedu.grpc.exercise2;

import com.springbootedu.grpc.exercise1.BookRepository;
import com.springbootedu.grpc.exercises.v1.Book;
import com.springbootedu.grpc.exercises.v1.OrderLine;
import com.springbootedu.grpc.exercises.v1.OrderSummary;
import io.grpc.stub.StreamObserver;
import java.util.Optional;

/**
 * Exercise 2 — receives the order lines of one client stream and answers with one summary at the end.
 */
public class OrderLinesObserver implements StreamObserver<OrderLine> {

    private final BookRepository books;
    private final StreamObserver<OrderSummary> responses;
    private int accepted;
    private int rejected;
    private long totalCents;

    public OrderLinesObserver(BookRepository books, StreamObserver<OrderSummary> responses) {
        this.books = books;
        this.responses = responses;
    }

    @Override
    public void onNext(OrderLine line) {
        Optional<Book> book = books.find(line.getIsbn());
        if (book.isEmpty() || line.getQuantity() < 1) {
            rejected++;
            return;
        }
        accepted++;
        totalCents += book.get().getPriceCents() * line.getQuantity();
    }

    @Override
    public void onError(Throwable error) {
        // the client gave up: there is nobody to answer
    }

    @Override
    public void onCompleted() {
        responses.onNext(OrderSummary.newBuilder()
                .setAcceptedLines(accepted).setRejectedLines(rejected).setTotalCents(totalCents).build());
        responses.onCompleted();
    }
}
