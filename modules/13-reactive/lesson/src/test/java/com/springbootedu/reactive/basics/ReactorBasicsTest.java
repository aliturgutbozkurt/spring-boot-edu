package com.springbootedu.reactive.basics;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

/**
 * Lessons 3.1–3.3 — Mono and Flux, operators, errors, time and backpressure, verified step by step.
 */
class ReactorBasicsTest {

    private final ReactorBasics basics = new ReactorBasics();

    @Test
    void nothingHappensUntilSomebodySubscribes() {
        Flux<String> titles = basics.titlesInCapitals();        // only a description of the work

        StepVerifier.create(titles)                              // subscribes
                .expectNext("EFFECTIVE JAVA", "SPRING IN ACTION", "JAVA PUZZLERS")
                .verifyComplete();
    }

    @Test
    void flatMapRunsInnerPublishersConcurrently() {
        StepVerifier.create(basics.pricesOf("9780134685991", "9781617297571").collectList())
                .assertNext(prices -> assertThat(prices).hasSize(2))
                .verifyComplete();
    }

    @Test
    void anErrorCanBeReplacedByAFallback() {
        StepVerifier.create(basics.priceOrFallback("unknown"))
                .expectNext(0.0)
                .verifyComplete();
    }

    @Test
    void aFailingSourceIsRetried() {
        StepVerifier.create(basics.flakyPriceWithRetry())
                .expectNext(89.90)                              // failed twice, succeeded on the third try
                .verifyComplete();
    }

    @Test
    void virtualTimeTestsSlowStreamsInstantly() {
        StepVerifier.withVirtualTime(() -> basics.tick(Duration.ofMinutes(1)).take(3))
                .expectSubscription()
                .thenAwait(Duration.ofMinutes(3))                // no real waiting
                .expectNext(0L, 1L, 2L)
                .verifyComplete();
    }

    @Test
    void theSubscriberControlsTheSpeedWithBackpressure() {
        StepVerifier.create(basics.numbers(1_000), 0)           // request nothing at first
                .expectSubscription()
                .thenRequest(2)
                .expectNext(1, 2)
                .thenRequest(1)
                .expectNext(3)
                .thenCancel()                                    // the other 997 are never produced
                .verify();
        assertThat(basics.produced()).isEqualTo(3);
    }
}
