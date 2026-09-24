package com.springbootedu.reactive.exercise3;

import java.time.Duration;
import reactor.core.publisher.Mono;

/**
 * Exercise 3 — loads the product page from three sources at the same time.
 */
public class ProductPage {

    private final PriceService prices;
    private final StockService stock;
    private final RatingService ratings;

    public ProductPage(PriceService prices, StockService stock, RatingService ratings) {
        this.prices = prices;
        this.stock = stock;
        this.ratings = ratings;
    }

    public Mono<ProductView> load(String isbn) {
        Mono<Double> rating = ratings.ratingOf(isbn)
                .timeout(Duration.ofSeconds(1))
                .onErrorReturn(0.0)
                .defaultIfEmpty(0.0);
        return Mono.zip(prices.priceOf(isbn), stock.inStock(isbn), rating)
                .map(all -> new ProductView(isbn, all.getT1(), all.getT2(), all.getT3()));
    }
}
