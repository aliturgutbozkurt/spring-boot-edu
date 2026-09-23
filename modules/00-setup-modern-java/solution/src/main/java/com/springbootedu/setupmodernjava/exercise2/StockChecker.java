package com.springbootedu.setupmodernjava.exercise2;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Exercise 2 — asks the stock service about many books at once.
 */
public class StockChecker {

    private final StockClient client;

    public StockChecker(StockClient client) {
        this.client = client;
    }

    public Map<String, Integer> checkAll(List<String> isbns) {
        Map<String, Future<Integer>> pending = new LinkedHashMap<>();
        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            for (String isbn : isbns) {
                pending.put(isbn, executor.submit(() -> client.stockOf(isbn)));
            }
        }
        Map<String, Integer> stock = new LinkedHashMap<>();
        pending.forEach((isbn, future) -> stock.put(isbn, resultOf(future)));
        return stock;
    }

    private static int resultOf(Future<Integer> future) {
        try {
            return future.get();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException(e);
        } catch (ExecutionException e) {
            throw new IllegalStateException(e.getCause());
        }
    }
}
