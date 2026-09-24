package com.springbootedu.reactive.exercise3;

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
        // TODO 3a: the rating is optional: no rating, an error or more than 1 second → 0.0
        // TODO 3b: ask the three services at the same time and combine their answers into a ProductView
        // TODO 3c: if there is no price, the whole result is empty
        throw new UnsupportedOperationException("TODO 3 — " + isbn + prices + stock + ratings);
    }
}
