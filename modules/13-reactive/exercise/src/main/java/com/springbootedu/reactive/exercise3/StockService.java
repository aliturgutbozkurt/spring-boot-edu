package com.springbootedu.reactive.exercise3;

import reactor.core.publisher.Mono;

/**
 * Given: how many copies are in stock.
 */
@FunctionalInterface
public interface StockService {

    Mono<Integer> inStock(String isbn);
}
