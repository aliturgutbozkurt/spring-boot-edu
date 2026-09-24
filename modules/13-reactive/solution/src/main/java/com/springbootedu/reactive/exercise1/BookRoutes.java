package com.springbootedu.reactive.exercise1;

import static org.springframework.web.reactive.function.server.RouterFunctions.route;

import java.net.URI;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

/**
 * Exercise 1 — the book API as functional endpoints.
 */
@Configuration(proxyBeanMethods = false)
public class BookRoutes {

    @Bean
    RouterFunction<ServerResponse> bookRouter(BookStore books) {
        return route()
                .path("/api/books", api -> api
                        .GET("", request -> ServerResponse.ok().body(books.findAll(), Book.class))
                        .GET("/{isbn}", request -> books.find(request.pathVariable("isbn"))
                                .flatMap(book -> ServerResponse.ok().bodyValue(book))
                                .switchIfEmpty(ServerResponse.notFound().build()))
                        .POST("", request -> request.bodyToMono(Book.class)
                                .flatMap(books::save)
                                .flatMap(saved -> ServerResponse.created(URI.create("/api/books/" + saved.isbn()))
                                        .bodyValue(saved)))
                        .DELETE("/{isbn}", request -> books.delete(request.pathVariable("isbn"))
                                .flatMap(deleted -> deleted
                                        ? ServerResponse.noContent().build()
                                        : ServerResponse.notFound().build())))
                .build();
    }
}
