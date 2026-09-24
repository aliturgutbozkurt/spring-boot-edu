package com.springbootedu.modulith.order;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import com.springbootedu.modulith.TestcontainersConfiguration;
import com.springbootedu.modulith.catalog.Book;
import com.springbootedu.modulith.catalog.CatalogService;
import java.math.BigDecimal;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.modulith.test.ApplicationModuleTest;
import org.springframework.modulith.test.Scenario;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@ApplicationModuleTest                                    // only the order module: no inventory beans
@Import(TestcontainersConfiguration.class)
class Exercise3OrderTest {

    @MockitoBean
    CatalogService catalog;

    @Autowired
    OrderService orders;

    @Test
    void placingAnOrderPublishesOrderPlaced(Scenario scenario) {
        given(catalog.find("9781617297571"))
                .willReturn(Optional.of(new Book("9781617297571", "Spring in Action", new BigDecimal("95.00"))));

        scenario.stimulate(() -> orders.place("c-3", "9781617297571", 2))
                .andWaitForEventOfType(OrderPlaced.class)
                .matching(event -> event.customerId().equals("c-3"))
                .toArriveAndVerify((event, order) -> {
                    assertThat(event.orderId()).isEqualTo(order.id());
                    assertThat(event.quantity()).isEqualTo(2);
                    assertThat(event.total()).isEqualByComparingTo("190.00");
                });
    }
}
