package com.springbootedu.datajdbcpostgres.order;

import static org.assertj.core.api.Assertions.assertThat;

import com.springbootedu.datajdbcpostgres.TestcontainersConfiguration;
import java.math.BigDecimal;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jdbc.test.autoconfigure.DataJdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.simple.JdbcClient;

/**
 * Lesson 3.4 — Spring Data JDBC: an aggregate is saved and loaded as a whole.
 */
@DataJdbcTest
@Import(TestcontainersConfiguration.class)
class PurchaseOrderRepositoryTest {

    @Autowired
    PurchaseOrderRepository orders;

    @Autowired
    JdbcClient jdbc;

    private static PurchaseOrder newOrder(String email) {
        return PurchaseOrder.create(email, Set.of(
                new OrderLine("9780134685991", 1, new BigDecimal("89.90")),
                new OrderLine("9780321336781", 2, new BigDecimal("55.00"))));
    }

    @Test
    void savesTheRootAndItsLinesTogether() {
        PurchaseOrder saved = orders.save(newOrder("ayse@example.com"));

        assertThat(saved.id()).isNotNull();
        assertThat(jdbc.sql("select count(*) from order_line where purchase_order = ?").param(saved.id())
                .query(Integer.class).single()).isEqualTo(2);
    }

    @Test
    void loadsTheWholeAggregate() {
        Long id = orders.save(newOrder("ayse@example.com")).id();

        PurchaseOrder loaded = orders.findById(id).orElseThrow();

        assertThat(loaded.lines()).hasSize(2);
        assertThat(loaded.total()).isEqualByComparingTo("199.90");
    }

    @Test
    void derivedQueryByCustomer() {
        orders.save(newOrder("ayse@example.com"));
        orders.save(newOrder("mehmet@example.com"));

        assertThat(orders.findByCustomerEmail("ayse@example.com")).hasSize(1);
    }

    @Test
    void customQueryForAggregatesAboveATotal() {
        orders.save(newOrder("ayse@example.com"));
        orders.save(PurchaseOrder.create("ucuz@example.com",
                Set.of(new OrderLine("9780321336781", 1, new BigDecimal("55.00")))));

        assertThat(orders.findIdsWithTotalAbove(new BigDecimal("100"))).hasSize(1);
    }

    @Test
    void deletingTheRootDeletesTheLines() {
        Long id = orders.save(newOrder("ayse@example.com")).id();

        orders.deleteById(id);

        assertThat(jdbc.sql("select count(*) from order_line where purchase_order = ?").param(id)
                .query(Integer.class).single()).isZero();
    }
}
