package com.springbootedu.reactive.exercise3;

import java.math.BigDecimal;
import reactor.core.publisher.Mono;

/**
 * Given: the price of a book — empty if the book is unknown.
 */
@FunctionalInterface
public interface PriceService {

    Mono<BigDecimal> priceOf(String isbn);
}
