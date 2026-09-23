package com.springbootedu.datajpapostgres.exercise1;

import static org.assertj.core.api.Assertions.assertThat;

import com.springbootedu.datajpapostgres.TestcontainersConfiguration;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.context.annotation.Import;

/**
 * Exercise 1 — mapping the order aggregate: one-to-many with cascade and orphan removal.
 */
@DataJpaTest
@Import(TestcontainersConfiguration.class)
class Exercise1Test {

    @Autowired
    TestEntityManager entityManager;

    private PurchaseOrder reload(Long id) {
        entityManager.flush();
        entityManager.clear();                                   // forget everything, read from the database
        return entityManager.find(PurchaseOrder.class, id);
    }

    @Test
    void loadsAnOrderWithItsLines() {
        PurchaseOrder order = entityManager.find(PurchaseOrder.class, 1L);

        assertThat(order.getLines()).hasSize(2);
        assertThat(order.total()).isEqualByComparingTo("199.90");
        assertThat(order.getStatus()).isEqualTo(OrderStatus.NEW);
    }

    @Test
    void savingTheOrderSavesItsLines() {
        PurchaseOrder order = new PurchaseOrder("zeynep@example.com");
        order.addLine("9780134685991", 2, new BigDecimal("89.90"));
        order.addLine("9780134757599", 1, new BigDecimal("85.00"));

        Long id = entityManager.persistAndGetId(order, Long.class);   // one persist, cascaded to the lines

        assertThat(reload(id).getLines()).hasSize(2);
    }

    @Test
    void aLineRemovedFromTheOrderIsDeleted() {
        PurchaseOrder order = entityManager.find(PurchaseOrder.class, 1L);
        order.removeLine(order.getLines().getFirst());

        assertThat(reload(1L).getLines()).hasSize(1);
    }

    @Test
    void everyLineKnowsItsOrder() {
        PurchaseOrder order = new PurchaseOrder("zeynep@example.com");
        order.addLine("9780134685991", 1, new BigDecimal("89.90"));

        assertThat(order.getLines().getFirst().getOrder()).isSameAs(order);
    }
}
