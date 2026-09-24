package com.springbootedu.reactive.book;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

/**
 * Lesson 3.4 — the reactive twin of CrudRepository: every method returns a Mono or a Flux.
 */
// tag::repository[]
public interface BookRepository extends ReactiveCrudRepository<Book, Long> {

    Mono<Book> findByIsbn(String isbn);
}
// end::repository[]
