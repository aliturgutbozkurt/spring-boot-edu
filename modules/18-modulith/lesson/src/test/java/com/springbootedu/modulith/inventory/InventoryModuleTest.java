package com.springbootedu.modulith.inventory;

import static org.assertj.core.api.Assertions.assertThat;

import com.springbootedu.modulith.TestcontainersConfiguration;
import com.springbootedu.modulith.order.OrderPlaced;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.modulith.test.ApplicationModuleTest;
import org.springframework.modulith.test.Scenario;
import org.springframework.test.context.TestPropertySource;

/**
 * Lesson 3.5 — the inventory module reacts to an event; the test publishes the event itself.
 */
@ApplicationModuleTest
@Import(TestcontainersConfiguration.class)
@TestPropertySource(properties = "bookstore.tour.enabled=false")    // the tour needs all modules
class InventoryModuleTest {

    private static final String SPRING_IN_ACTION = "9781617297571";

    @Autowired
    Inventory inventory;

    // tag::scenario-publish[]
    @Test
    void anOrderReservesStock(Scenario scenario) {
        int before = inventory.available(SPRING_IN_ACTION);

        scenario.publish(new OrderPlaced(1000, "c-2", SPRING_IN_ACTION, 3, new BigDecimal("285.00")))
                .andWaitForStateChange(() -> inventory.available(SPRING_IN_ACTION), available -> available != before)
                .andVerify(available -> assertThat(available).isEqualTo(before - 3));
    }
    // end::scenario-publish[]
}
