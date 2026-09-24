package com.springbootedu.reactive.review;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

/**
 * Lesson 3.5 — a reactive MongoDB repository.
 */
public interface ReviewRepository extends ReactiveMongoRepository<Review, String> {

    Flux<Review> findByIsbnOrderByStarsDesc(String isbn);
}
