package com.springbootedu.datajdbcpostgres.checkout;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.springbootedu.datajdbcpostgres.TestcontainersConfiguration;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.simple.JdbcClient;

/**
 * Lessons 3.5–3.6 — transactions and transactional events against a real database.
 * No test-managed transaction here: we want to see real commits and rollbacks.
 */
@SpringBootTest(properties = "bookstore.tour.enabled=false")
@Import(TestcontainersConfiguration.class)
class CheckoutServiceTest {

    @Autowired
    CheckoutService checkout;

    @Autowired
    JdbcClient jdbc;

    @BeforeEach
    void cleanDatabase() {
        jdbc.sql("delete from purchase_order").update();          // order_line rows go with ON DELETE CASCADE
        jdbc.sql("delete from audit_log").update();
        jdbc.sql("delete from notification").update();
        jdbc.sql("update book set stock = 5 where isbn = '9780134685991'").update();
    }

    private int count(String table) {
        return jdbc.sql("select count(*) from " + table).query(Integer.class).single();
    }

    private int stockOfEffectiveJava() {
        return jdbc.sql("select stock from book where isbn = '9780134685991'").query(Integer.class).single();
    }

    @Test
    void aSuccessfulCheckoutCommitsEverything() {
        long orderId = checkout.placeOrder("ayse@example.com", Map.of("9780134685991", 2));

        assertThat(orderId).isPositive();
        assertThat(stockOfEffectiveJava()).isEqualTo(3);
        assertThat(count("purchase_order")).isEqualTo(1);
        assertThat(count("audit_log")).isEqualTo(1);
        assertThat(count("notification")).isEqualTo(1);            // written AFTER the commit
    }

    @Test
    void notEnoughStockRollsBackTheWholeCheckout() {
        assertThatThrownBy(() -> checkout.placeOrder("ayse@example.com", Map.of("9780134685991", 99)))
                .isInstanceOf(OutOfStockException.class);

        assertThat(stockOfEffectiveJava()).isEqualTo(5);           // unchanged
        assertThat(count("purchase_order")).isZero();              // no half-written order
        assertThat(count("notification")).isZero();                // no commit → no after-commit listener
    }

    @Test
    void theAuditEntrySurvivesTheRollback() {
        assertThatThrownBy(() -> checkout.placeOrder("ayse@example.com", Map.of("9780134685991", 99)))
                .isInstanceOf(OutOfStockException.class);

        assertThat(count("audit_log")).isEqualTo(1);               // REQUIRES_NEW: its own transaction
    }
}
