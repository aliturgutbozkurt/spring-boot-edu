package com.springbootedu.datajpapostgres.exercise2;

import static org.assertj.core.api.Assertions.assertThat;

import com.springbootedu.datajpapostgres.TestcontainersConfiguration;
import com.springbootedu.datajpapostgres.exercise1.PurchaseOrder;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import java.math.BigDecimal;
import java.util.List;
import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;

/**
 * Exercise 2 — find and fix an N+1 problem; an aggregate report query.
 */
@DataJpaTest
@Import(TestcontainersConfiguration.class)
class Exercise2Test {

    @Autowired
    OrderRepository orders;

    @Autowired
    EntityManager entityManager;

    @Autowired
    EntityManagerFactory entityManagerFactory;

    Statistics statistics;

    @BeforeEach
    void startCounting() {
        entityManager.clear();
        statistics = entityManagerFactory.unwrap(SessionFactory.class).getStatistics();
        statistics.clear();
    }

    private static BigDecimal sumOfTotals(List<PurchaseOrder> list) {
        return list.stream().map(PurchaseOrder::total).reduce(BigDecimal.ZERO, BigDecimal::add);   // touches the lines
    }

    @Test
    void theProblem_oneQueryPerOrder() {
        List<PurchaseOrder> list = orders.findByCustomerEmail("ayse@example.com");

        assertThat(sumOfTotals(list)).isEqualByComparingTo("374.80");
        assertThat(statistics.getPrepareStatementCount()).isEqualTo(1 + 3);
    }

    @Test
    void theFix_oneQueryInTotal() {
        List<PurchaseOrder> list = orders.findWithLinesByCustomerEmail("ayse@example.com");

        assertThat(sumOfTotals(list)).isEqualByComparingTo("374.80");
        assertThat(statistics.getPrepareStatementCount()).isEqualTo(1);
    }

    @Test
    void totalsPerCustomerAreComputedByTheDatabase() {
        assertThat(orders.totalsPerCustomer()).containsExactly(
                new CustomerTotal("ayse@example.com", new BigDecimal("374.80")),
                new CustomerTotal("mehmet@example.com", new BigDecimal("279.90")));
        assertThat(statistics.getPrepareStatementCount()).isEqualTo(1);
    }
}
