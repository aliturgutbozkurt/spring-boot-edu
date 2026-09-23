package com.springbootedu.datajpapostgres.exercise3;

import static org.assertj.core.api.Assertions.assertThat;

import com.springbootedu.datajpapostgres.TestcontainersConfiguration;
import com.springbootedu.datajpapostgres.exercise1.OrderStatus;
import java.time.Instant;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

/**
 * Exercise 3 — a dynamic order search with specifications.
 */
@DataJpaTest
@Import({TestcontainersConfiguration.class, OrderSearch.class})
class Exercise3Test {

    @Autowired
    OrderSearch search;

    private static final PageRequest BY_ID = PageRequest.of(0, 10, Sort.by("id"));

    private java.util.List<Long> ids(OrderFilter filter) {
        return search.search(filter, BY_ID).map(OrderSummary::id).getContent();
    }

    @Test
    void withoutCriteriaEveryOrderIsFound() {
        assertThat(ids(new OrderFilter(null, null, null, null))).containsExactly(1L, 2L, 3L, 4L);
    }

    @Test
    void byStatus() {
        assertThat(ids(new OrderFilter(null, OrderStatus.PAID, null, null))).containsExactly(2L, 4L);
    }

    @Test
    void ordersContainingABook() {
        assertThat(ids(new OrderFilter(null, null, null, "9780134685991"))).containsExactly(1L, 3L, 4L);
    }

    @Test
    void customerAndDateTogether() {
        assertThat(ids(new OrderFilter("ayse@example.com", null, Instant.parse("2026-09-05T00:00:00Z"), null)))
                .containsExactly(2L, 3L);
    }

    @Test
    void theSummaryCarriesCustomerAndStatus() {
        assertThat(search.search(new OrderFilter("mehmet@example.com", null, null, null), BY_ID).getContent())
                .containsExactly(new OrderSummary(4L, "mehmet@example.com", OrderStatus.PAID));
    }
}
