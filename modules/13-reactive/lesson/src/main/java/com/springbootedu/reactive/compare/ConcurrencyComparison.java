package com.springbootedu.reactive.compare;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Lesson 3.7 — the same job twice: many slow calls, first reactive, then with virtual threads.
 */
public class ConcurrencyComparison {

    public record Result(int results, Duration elapsed) {
    }

    private final Duration latency;

    public ConcurrencyComparison(Duration latency) {
        this.latency = latency;
    }

    // tag::reactive[]
    public Result reactive(int calls) {
        long start = System.nanoTime();
        Long count = Flux.range(1, calls)
                .flatMap(i -> Mono.just(i).delayElement(latency), calls)       // waits without a thread
                .count()
                .block();                                                      // only here: to measure
        return new Result(count == null ? 0 : count.intValue(), Duration.ofNanos(System.nanoTime() - start));
    }
    // end::reactive[]

    // tag::virtual-threads[]
    public Result virtualThreads(int calls) {
        long start = System.nanoTime();
        int results = 0;
        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            List<Future<Integer>> futures = new ArrayList<>();
            for (int i = 1; i <= calls; i++) {
                int call = i;
                futures.add(executor.submit(() -> {
                    Thread.sleep(latency);                                     // plain blocking code
                    return call;
                }));
            }
            for (Future<Integer> future : futures) {
                future.get();
                results++;
            }
        } catch (InterruptedException | ExecutionException e) {
            throw new IllegalStateException(e);
        }
        return new Result(results, Duration.ofNanos(System.nanoTime() - start));
    }
    // end::virtual-threads[]
}
