package com.springbootedu.modulith.loyalty;

import static org.assertj.core.api.Assertions.assertThat;

import com.springbootedu.modulith.ModuleDependencies;
import com.springbootedu.modulith.TestcontainersConfiguration;
import com.springbootedu.modulith.order.OrderPlaced;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.modulith.test.ApplicationModuleTest;
import org.springframework.modulith.test.Scenario;

@ApplicationModuleTest                                    // only the loyalty module
@Import(TestcontainersConfiguration.class)
class Exercise2Test {

    @Autowired
    LoyaltyPoints loyalty;

    @Test
    void anOrderGivesOnePointForEveryFullTen(Scenario scenario) {
        scenario.publish(new OrderPlaced(1, "c-1", "9780134685991", 2, new BigDecimal("179.80")))
                .andWaitForStateChange(() -> loyalty.pointsOf("c-1"), points -> points > 0)
                .andVerify(points -> assertThat(points).isEqualTo(17));
    }

    @Test
    void pointsAddUp(Scenario scenario) {
        scenario.publish(new OrderPlaced(2, "c-2", "9781449373320", 1, new BigDecimal("110.00")))
                .andWaitForStateChange(() -> loyalty.pointsOf("c-2"), points -> points == 11)
                .andVerify(points -> assertThat(points).isEqualTo(11));

        scenario.publish(new OrderPlaced(3, "c-2", "9780321336781", 1, new BigDecimal("55.00")))
                .andWaitForStateChange(() -> loyalty.pointsOf("c-2"), points -> points > 11)
                .andVerify(points -> assertThat(points).isEqualTo(16));
    }

    @Test
    void aCustomerWithoutOrdersHasNoPoints() {
        assertThat(loyalty.pointsOf("nobody")).isZero();
    }

    @Test
    void theLoyaltyModuleDependsOnTheOrderModuleOnly() {
        assertThat(ModuleDependencies.of("loyalty")).containsExactly("order");
    }
}
