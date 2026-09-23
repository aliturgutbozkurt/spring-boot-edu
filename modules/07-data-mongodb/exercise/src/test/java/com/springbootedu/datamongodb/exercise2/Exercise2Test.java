package com.springbootedu.datamongodb.exercise2;

import static org.assertj.core.api.Assertions.assertThat;

import com.springbootedu.datamongodb.TestcontainersConfiguration;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.mongodb.test.autoconfigure.DataMongoTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.core.MongoTemplate;

/**
 * Exercise 2 — a revenue report computed by an aggregation pipeline.
 */
@DataMongoTest
@Import({TestcontainersConfiguration.class, SalesReport.class})
class Exercise2Test {

    @Autowired
    SalesReport report;

    @Autowired
    MongoTemplate mongo;

    private static Sale sale(String category, int quantity, String unitPrice, String soldAt) {
        return new Sale(null, category, quantity, new BigDecimal(unitPrice), Instant.parse(soldAt));
    }

    @BeforeEach
    void seed() {
        mongo.dropCollection(Sale.class);
        mongo.insertAll(List.of(
                sale("book", 2, "89.90", "2026-09-01T10:00:00Z"),      // 179.80
                sale("book", 1, "55.00", "2026-09-02T10:00:00Z"),      //  55.00
                sale("bag", 1, "450.00", "2026-09-03T10:00:00Z"),      // 450.00
                sale("pen", 3, "20.00", "2026-09-04T10:00:00Z"),       //  60.00
                sale("bag", 2, "150.00", "2026-10-01T10:00:00Z")));    // outside September
    }

    @Test
    void revenuePerCategoryForAPeriodHighestFirst() {
        List<CategoryRevenue> september = report.revenuePerCategory(
                Instant.parse("2026-09-01T00:00:00Z"), Instant.parse("2026-10-01T00:00:00Z"));

        assertThat(september).containsExactly(
                new CategoryRevenue("bag", 1, new BigDecimal("450.00")),
                new CategoryRevenue("book", 3, new BigDecimal("234.80")),
                new CategoryRevenue("pen", 3, new BigDecimal("60.00")));
    }

    @Test
    void theEndOfThePeriodIsExclusive() {
        assertThat(report.revenuePerCategory(Instant.parse("2026-10-01T00:00:00Z"), Instant.parse("2026-11-01T00:00:00Z")))
                .containsExactly(new CategoryRevenue("bag", 2, new BigDecimal("300.00")));
    }
}
