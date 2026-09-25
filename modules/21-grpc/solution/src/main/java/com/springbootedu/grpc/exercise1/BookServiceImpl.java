package com.springbootedu.grpc.exercise1;

import com.springbootedu.grpc.exercise2.OrderLinesObserver;
import com.springbootedu.grpc.exercises.v1.Book;
import com.springbootedu.grpc.exercises.v1.BookServiceGrpc;
import com.springbootedu.grpc.exercises.v1.GetBookRequest;
import com.springbootedu.grpc.exercises.v1.OrderLine;
import com.springbootedu.grpc.exercises.v1.OrderSummary;
import com.springbootedu.grpc.exercises.v1.SearchBooksRequest;
import com.springbootedu.grpc.exercises.v1.SearchBooksResponse;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import java.util.Locale;
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

    @Override
    public void searchBooks(SearchBooksRequest request, StreamObserver<SearchBooksResponse> responses) {
        String part = request.getTitleContains().toLowerCase(Locale.ROOT);
        SearchBooksResponse.Builder result = SearchBooksResponse.newBuilder();
        books.all().stream()
                .filter(book -> book.getTitle().toLowerCase(Locale.ROOT).contains(part))
                .forEach(result::addBooks);
        responses.onNext(result.build());
        responses.onCompleted();
    }

    @Override
    public StreamObserver<OrderLine> placeOrders(StreamObserver<OrderSummary> responses) {
        return new OrderLinesObserver(books, responses);
    }
}
