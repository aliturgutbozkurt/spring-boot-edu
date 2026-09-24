package com.springbootedu.modulith.notification;

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
 * Lesson 3.3 — a second, independent listener of the same event.
 */
@ApplicationModuleTest
@Import(TestcontainersConfiguration.class)
@TestPropertySource(properties = "bookstore.tour.enabled=false")    // the tour needs all modules
class NotificationModuleTest {

    @Autowired
    Mailbox mailbox;

    @Test
    void theCustomerGetsAConfirmation(Scenario scenario) {
        scenario.publish(new OrderPlaced(2000, "c-5", "9780134685991", 1, new BigDecimal("89.90")))
                .andWaitForStateChange(() -> mailbox.sentTo("c-5"), mails -> !mails.isEmpty())
                .andVerify(mails -> assertThat(mails).singleElement().asString().contains("order 2000"));
    }
}
