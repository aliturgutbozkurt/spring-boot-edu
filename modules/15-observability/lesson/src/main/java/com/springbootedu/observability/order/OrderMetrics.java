package com.springbootedu.observability.order;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;
import org.springframework.stereotype.Component;

/**
 * Lesson 3.2 — business metrics: how many orders, how long they take, how many are in progress.
 */
// tag::metrics[]
@Component
public class OrderMetrics {

    private final MeterRegistry registry;
    private final Timer processing;
    private final AtomicInteger inProgress = new AtomicInteger();

    public OrderMetrics(MeterRegistry registry) {
        this.registry = registry;
        this.processing = Timer.builder("bookstore.order.processing")
                .description("Time to place an order")
                .publishPercentiles(0.5, 0.95)                              // median and p95
                .register(registry);
        Gauge.builder("bookstore.orders.in.progress", inProgress, AtomicInteger::get)   // current value, read on demand
                .register(registry);
    }

    public <T> T recordOrder(String channel, Supplier<T> placeOrder) {
        inProgress.incrementAndGet();
        try {
            T result = processing.record(placeOrder);
            Counter.builder("bookstore.orders.placed")
                    .tag("channel", channel)                                // few values only: web, app
                    .register(registry)                                     // returns the existing counter
                    .increment();
            return result;
        } finally {
            inProgress.decrementAndGet();
        }
    }
}
// end::metrics[]
