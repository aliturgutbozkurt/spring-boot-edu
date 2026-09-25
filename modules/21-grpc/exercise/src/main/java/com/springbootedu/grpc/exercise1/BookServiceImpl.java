package com.springbootedu.grpc.exercise1;

import com.springbootedu.grpc.exercise2.OrderLinesObserver;
import com.springbootedu.grpc.exercises.v1.Book;
import com.springbootedu.grpc.exercises.v1.BookServiceGrpc;
import com.springbootedu.grpc.exercises.v1.GetBookRequest;
import com.springbootedu.grpc.exercises.v1.OrderLine;
import com.springbootedu.grpc.exercises.v1.OrderSummary;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import org.springframework.grpc.server.service.GrpcService;

/**
 * Exercises 1 and 2 — the server side of BookService.
 */
@GrpcService
class BookServiceImpl extends BookServiceGrpc.BookServiceImplBase {

    private final BookRepository books;

    BookServiceImpl(BookRepository books) {
        this.books = books;
    }

    @Override
    public void getBook(GetBookRequest request, StreamObserver<Book> responses) {
        books.find(request.getIsbn()).ifPresentOrElse(book -> {
            responses.onNext(book);
            responses.onCompleted();
        }, () -> responses.onError(Status.NOT_FOUND.withDescription(request.getIsbn()).asRuntimeException()));
    }

    // TODO 1b: implement SearchBooks: all books whose title contains title_contains (ignoring case), in one response
    //          (without an override, the generated base class answers UNIMPLEMENTED)

    @Override
    public StreamObserver<OrderLine> placeOrders(StreamObserver<OrderSummary> responses) {
        return new OrderLinesObserver(books, responses);
    }
}
