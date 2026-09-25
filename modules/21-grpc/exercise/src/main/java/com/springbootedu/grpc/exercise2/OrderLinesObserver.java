package com.springbootedu.grpc.exercise2;

import com.springbootedu.grpc.exercise1.BookRepository;
import com.springbootedu.grpc.exercises.v1.OrderLine;
import com.springbootedu.grpc.exercises.v1.OrderSummary;
import io.grpc.stub.StreamObserver;

/**
 * Exercise 2 — receives the order lines of one client stream and answers with one summary at the end.
 */
public class OrderLinesObserver implements StreamObserver<OrderLine> {

    private final BookRepository books;
    private final StreamObserver<OrderSummary> responses;

    public OrderLinesObserver(BookRepository books, StreamObserver<OrderSummary> responses) {
        this.books = books;
        this.responses = responses;
    }

    @Override
    public void onNext(OrderLine line) {
        // TODO 2a: an unknown book or a quantity below 1 is a rejected line;
        //          otherwise the line is accepted and adds price × quantity to the total
    }

    @Override
    public void onError(Throwable error) {
        // the client gave up: there is nobody to answer
    }

    @Override
    public void onCompleted() {
        // TODO 2b: the client has sent all lines: answer with ONE OrderSummary, then complete the call
        throw new UnsupportedOperationException("TODO 2 — " + books + responses);
    }
}
