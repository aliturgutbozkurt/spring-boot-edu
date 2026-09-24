package com.springbootedu.reactive.book;

import static org.springframework.web.reactive.function.server.RouterFunctions.route;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

/**
 * Lesson 3.4 — functional endpoints: the routing is plain code instead of annotations.
 */
// tag::routes[]
@Configuration(proxyBeanMethods = false)
class BookRoutes {

    @Bean
    RouterFunction<ServerResponse> bookRouter(BookHandler books) {
        return route()
                .path("/api/books", builder -> builder
                        .GET("", books::all)
                        .GET("/{isbn}", books::one)
                        .POST("", books::create))
                .build();
    }
}
// end::routes[]
