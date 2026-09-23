package com.springbootedu.datajpapostgres.stock;

import static org.assertj.core.api.Assertions.assertThat;

import com.springbootedu.datajpapostgres.TestcontainersConfiguration;
import com.springbootedu.datajpapostgres.catalog.BookRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.simple.JdbcClient;

/**
 * Lesson 3.7 — pessimistic locking: ten buyers, five copies, no overselling.
 */
@SpringBootTest(properties = "bookstore.tour.enabled=false")
@Import(TestcontainersConfiguration.class)
class StockServiceTest {

    private static final String ISBN = "9781617293566";                 // Modern Java in Action

    @Autowired
    StockService stock;

    @Autowired
    BookRepository books;

    @Autowired
    JdbcClient jdbc;

    @BeforeEach
    @AfterEach
    void resetStock() {
        jdbc.sql("update book set stock = 5 where isbn = ?").param(ISBN).update();
    }

    @Test
    void concurrentBuyersNeverOversell() throws Exception {
        List<Future<Boolean>> results = new ArrayList<>();
        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            for (int i = 0; i < 10; i++) {
                results.add(executor.submit(() -> stock.trySell(ISBN, 1)));
            }
        }

        long sold = 0;
        for (Future<Boolean> result : results) {
            if (result.get()) {
                sold++;
            }
        }
        assertThat(sold).isEqualTo(5);
        assertThat(books.findByIsbn(ISBN).orElseThrow().getStock()).isZero();
    }
}
