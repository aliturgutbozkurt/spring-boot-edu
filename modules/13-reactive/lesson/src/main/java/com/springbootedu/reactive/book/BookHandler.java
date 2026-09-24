package com.springbootedu.reactive.book;

import java.net.URI;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

/**
 * Lesson 3.4 — handler functions: a request goes in, a Mono of a response comes out.
 */
// tag::handler[]
@Component
class BookHandler {

    private final BookRepository books;

    BookHandler(BookRepository books) {
        this.books = books;
    }

    Mono<ServerResponse> all(ServerRequest request) {
        return ServerResponse.ok().body(books.findAll(), Book.class);          // streams the rows as they come
    }

    Mono<ServerResponse> one(ServerRequest request) {
        return books.findByIsbn(request.pathVariable("isbn"))
                .flatMap(book -> ServerResponse.ok().bodyValue(book))
                .switchIfEmpty(ServerResponse.notFound().build());            // empty Mono → 404
    }

    Mono<ServerResponse> create(ServerRequest request) {
        return request.bodyToMono(Book.class)
                .flatMap(books::save)
                .flatMap(saved -> ServerResponse.created(URI.create("/api/books/" + saved.isbn())).bodyValue(saved));
    }
}
// end::handler[]
