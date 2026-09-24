package com.springbootedu.modulith;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

import com.springbootedu.modulith.inventory.Inventory;
import com.springbootedu.modulith.inventory.Warehouse;
import com.springbootedu.modulith.order.Order;
import com.springbootedu.modulith.order.OrderPlaced;
import com.springbootedu.modulith.order.OrderService;
import java.time.Duration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.modulith.events.IncompleteEventPublications;

/**
 * Lesson 3.4 — a listener fails, the registry remembers it, and the event is delivered again later.
 */
@SpringBootTest(properties = "bookstore.tour.enabled=false")
@Import(TestcontainersConfiguration.class)
class EventPublicationRegistryTest {

    private static final String JAVA_PUZZLERS = "9780321336781";

    @Autowired
    OrderService orders;

    @Autowired
    Inventory inventory;

    @Autowired
    Warehouse warehouse;

    @Autowired
    IncompleteEventPublications incompletePublications;

    @Autowired
    JdbcClient jdbc;

    @AfterEach
    void warehouseBackOnline() {
        warehouse.goOnline();
    }

    // tag::resubmit[]
    @Test
    void aFailedListenerIsDeliveredAgainFromTheRegistry() {
        int before = inventory.available(JAVA_PUZZLERS);
        warehouse.goOffline();                                     // the inventory listener will throw

        Order order = orders.place("c-3", JAVA_PUZZLERS, 1);       // the order itself is committed

        await().atMost(Duration.ofSeconds(10))
                .untilAsserted(() -> assertThat(inventoryPublicationStatus(order.id())).isEqualTo("FAILED"));
        assertThat(inventory.available(JAVA_PUZZLERS)).isEqualTo(before);

        warehouse.goOnline();
        incompletePublications.resubmitIncompletePublications(
                publication -> publication.getEvent() instanceof OrderPlaced placed && placed.orderId() == order.id());

        await().atMost(Duration.ofSeconds(10))
                .untilAsserted(() -> assertThat(inventoryPublicationStatus(order.id())).isEqualTo("COMPLETED"));
        assertThat(inventory.available(JAVA_PUZZLERS)).isEqualTo(before - 1);
    }
    // end::resubmit[]

    /** One row per event AND listener: this is the row of the inventory listener for the order. */
    private String inventoryPublicationStatus(long orderId) {
        return jdbc.sql("""
                        SELECT status FROM event_publication
                        WHERE listener_id LIKE '%StockReservations%'
                          AND serialized_event::jsonb ->> 'orderId' = ?""")
                .param(String.valueOf(orderId))
                .query(String.class)
                .optional()
                .orElse("NOT PUBLISHED");
    }
}
