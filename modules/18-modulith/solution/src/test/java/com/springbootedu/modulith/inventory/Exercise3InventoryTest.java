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

@ApplicationModuleTest                                    // only the inventory module
@Import(TestcontainersConfiguration.class)
class Exercise3InventoryTest {

    @Autowired
    Inventory inventory;

    @Test
    void anOrderPlacedEventReservesStock(Scenario scenario) {
        int before = inventory.available("9780321336781");

        scenario.publish(new OrderPlaced(4, "c-4", "9780321336781", 5, new BigDecimal("275.00")))
                .andWaitForStateChange(() -> inventory.available("9780321336781"), available -> available != before)
                .andVerify(available -> assertThat(available).isEqualTo(before - 5));
    }
}
