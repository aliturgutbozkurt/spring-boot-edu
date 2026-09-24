package com.springbootedu.reactive.exercise3;

import reactor.core.publisher.Mono;

/**
 * Given: the average rating — may be empty (no ratings yet), fail, or be slow.
 */
@FunctionalInterface
public interface RatingService {

    Mono<Double> ratingOf(String isbn);
}
