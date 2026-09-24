package com.springbootedu.reactive.exercise3;

import java.math.BigDecimal;
import java.time.Duration;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

/**
 * Exercise 3 — a product page from three slow sources, loaded in parallel, robust against a failing source.
 */
class Exercise3Test {

    private static final Duration SLOW = Duration.ofMillis(300);

    private static ProductPage page(RatingService ratings) {
        PriceService prices = isbn -> Mono.just(new BigDecimal("89.90")).delayElement(SLOW);
        StockService stock = isbn -> Mono.just(12).delayElement(SLOW);
        return new ProductPage(prices, stock, ratings);
    }

    @Test
    void theThreeSourcesAreLoadedInParallel() {
        RatingService ratings = isbn -> Mono.just(4.5).delayElement(SLOW);

        StepVerifier.withVirtualTime(() -> page(ratings).load("9780134685991"))
                .expectSubscription()
                .expectNoEvent(Duration.ofMillis(299))
                .thenAwait(Duration.ofMillis(1))                         // 300 ms in total — not 900
                .expectNext(new ProductView("9780134685991", new BigDecimal("89.90"), 12, 4.5))
                .verifyComplete();
    }

    @Test
    void aBookWithoutRatingsGetsZero() {
        RatingService ratings = isbn -> Mono.empty();

        StepVerifier.withVirtualTime(() -> page(ratings).load("9780134685991"))
                .thenAwait(SLOW)
                .expectNextMatches(view -> view.rating() == 0.0)
                .verifyComplete();
    }

    @Test
    void aFailingRatingServiceDoesNotBreakThePage() {
        RatingService ratings = isbn -> Mono.error(new IllegalStateException("ratings are down"));

        StepVerifier.withVirtualTime(() -> page(ratings).load("9780134685991"))
                .thenAwait(SLOW)
                .expectNextMatches(view -> view.rating() == 0.0 && view.inStock() == 12)
                .verifyComplete();
    }

    @Test
    void aSlowRatingServiceIsCutOffAfterOneSecond() {
        RatingService ratings = isbn -> Mono.just(5.0).delayElement(Duration.ofSeconds(10));

        StepVerifier.withVirtualTime(() -> page(ratings).load("9780134685991"))
                .expectSubscription()
                .expectNoEvent(Duration.ofMillis(999))
                .thenAwait(Duration.ofMillis(1))
                .expectNextMatches(view -> view.rating() == 0.0)
                .verifyComplete();
    }

    @Test
    void aMissingPriceMeansThereIsNoPage() {
        PriceService noPrice = isbn -> Mono.empty();
        var page = new ProductPage(noPrice, isbn -> Mono.just(1), isbn -> Mono.just(1.0));

        StepVerifier.create(page.load("unknown")).verifyComplete();          // empty: the caller answers 404
    }
}
