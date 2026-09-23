package com.springbootedu.setupmodernjava.gatherers;

import java.util.List;
import java.util.stream.Gatherers;

/**
 * Lesson 3.8 — gatherers are custom intermediate stream operations; the JDK ships useful ones.
 */
public final class SalesStatistics {

    private SalesStatistics() {
    }

    // tag::gatherers[]
    public static List<List<String>> batches(List<String> isbns, int size) {
        return isbns.stream().gather(Gatherers.windowFixed(size)).toList();       // [a,b] [c,d] [e]
    }

    public static List<Double> movingAverage(List<Integer> dailySales, int days) {
        return dailySales.stream()
                .gather(Gatherers.windowSliding(days))                             // [10,20,30] [20,30,40]
                .map(window -> window.stream().mapToInt(Integer::intValue).average().orElse(0))
                .toList();
    }

    public static List<Integer> runningTotal(List<Integer> sales) {
        return sales.stream().gather(Gatherers.scan(() -> 0, Integer::sum)).toList();   // 5, 15, 35
    }
    // end::gatherers[]
}
