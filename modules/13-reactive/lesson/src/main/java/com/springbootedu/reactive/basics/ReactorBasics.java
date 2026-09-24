package com.springbootedu.reactive.basics;

import java.time.Duration;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicInteger;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

/**
 * Lessons 3.1–3.3 — the building blocks: a Mono has 0 or 1 element, a Flux 0 to many.
 */
public class ReactorBasics {

    private static final Map<String, Double> PRICES = Map.of("9780134685991", 89.90, "9781617297571", 95.00);

    private final AtomicInteger produced = new AtomicInteger();

    // tag::operators[]
    public Flux<String> titlesInCapitals() {
        return Flux.just("Effective Java", "Spring in Action", "Java Puzzlers")
                .map(String::toUpperCase);                             // runs only when someone subscribes
    }

    public Flux<Double> pricesOf(String... isbns) {
        return Flux.fromArray(isbns)
                .flatMap(this::lookupPrice);                           // all lookups run at the same time
    }

    Mono<Double> lookupPrice(String isbn) {
        Double price = PRICES.get(isbn);
        return price == null
                ? Mono.error(new NoSuchElementException("No price for " + isbn))
                : Mono.just(price).delayElement(Duration.ofMillis(100));   // a slow remote call, without blocking
    }
    // end::operators[]

    // tag::errors[]
    public Mono<Double> priceOrFallback(String isbn) {
        return lookupPrice(isbn)
                .onErrorResume(NoSuchElementException.class, e -> Mono.just(0.0));   // errors are signals too
    }

    public Mono<Double> flakyPriceWithRetry() {
        var attempts = new AtomicInteger();
        return Mono.defer(() -> attempts.incrementAndGet() < 3
                        ? Mono.<Double>error(new IllegalStateException("timeout"))
                        : Mono.just(89.90))
                .retryWhen(Retry.backoff(3, Duration.ofMillis(10)));       // resubscribe: 10 ms, 20 ms, …
    }
    // end::errors[]

    public Flux<Long> tick(Duration period) {
        return Flux.interval(period);                                  // 0, 1, 2, … once per period
    }

    // tag::backpressure[]
    public Flux<Integer> numbers(int count) {
        return Flux.range(1, count)
                .doOnNext(n -> produced.incrementAndGet());            // counts what was really produced
    }
    // end::backpressure[]

    public int produced() {
        return produced.get();
    }
}
