package com.springbootedu.setupmodernjava.exercise2;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;
import java.util.List;
import java.util.stream.IntStream;
import org.junit.jupiter.api.Test;

/**
 * Exercise 2 — many slow calls in parallel on virtual threads.
 */
class Exercise2Test {

    private final List<String> isbns = IntStream.range(0, 200).mapToObj(i -> "978-0-00-" + i).toList();

    @Test
    void returnsTheStockOfEveryIsbnInInputOrder() {
        var client = new SlowStockClient(Duration.ofMillis(10));

        var stock = new StockChecker(client).checkAll(isbns);

        assertThat(stock.keySet()).containsExactlyElementsOf(isbns);
        assertThat(stock).containsEntry("978-0-00-0", SlowStockClient.expectedStock("978-0-00-0"));
        assertThat(stock).containsEntry("978-0-00-199", SlowStockClient.expectedStock("978-0-00-199"));
    }

    @Test
    void callsRunInParallelOnVirtualThreads() {
        var client = new SlowStockClient(Duration.ofMillis(200));
        long start = System.nanoTime();

        new StockChecker(client).checkAll(isbns);

        assertThat(Duration.ofNanos(System.nanoTime() - start)).isLessThan(Duration.ofSeconds(3));   // sequential: 40 s
        assertThat(client.calls()).isEqualTo(200);
        assertThat(client.allCallsOnVirtualThreads()).isTrue();
    }
}
