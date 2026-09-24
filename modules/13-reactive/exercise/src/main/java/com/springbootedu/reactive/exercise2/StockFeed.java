package com.springbootedu.reactive.exercise2;

import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

/**
 * Given: publishes stock changes to everybody who listens right now (no replay).
 */
@Component
public class StockFeed {

    private final Sinks.Many<StockLevel> levels = Sinks.many().multicast().directBestEffort();

    public void publish(StockLevel level) {
        levels.tryEmitNext(level);
    }

    public Flux<StockLevel> changes() {
        return levels.asFlux();
    }
}
