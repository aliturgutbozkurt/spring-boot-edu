package com.springbootedu.setupmodernjava.virtualthreads;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Lesson 3.5 — thousands of blocking calls, one cheap virtual thread each.
 */
public final class PriceLookup {

    public record Result(int completed, Duration elapsed) {
    }

    private PriceLookup() {
    }

    // tag::virtual-threads[]
    public static Result lookupAll(int count, Duration latency) {
        long start = System.nanoTime();
        List<Future<Integer>> prices = new ArrayList<>();

        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {   // one new virtual thread per task
            for (int i = 0; i < count; i++) {
                prices.add(executor.submit(() -> fetchPrice(latency)));      // plain blocking code
            }
        }                                                                     // close() waits for all tasks

        int completed = (int) prices.stream().filter(Future::isDone).count();
        return new Result(completed, Duration.ofNanos(System.nanoTime() - start));
    }

    private static int fetchPrice(Duration latency) throws InterruptedException {
        Thread.sleep(latency);          // simulates a slow remote call; the carrier thread is released meanwhile
        return 42;
    }
    // end::virtual-threads[]

    public static boolean runsOnVirtualThread() {
        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            return executor.submit(() -> Thread.currentThread().isVirtual()).get();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        } catch (ExecutionException e) {
            throw new IllegalStateException(e);
        }
    }
}
