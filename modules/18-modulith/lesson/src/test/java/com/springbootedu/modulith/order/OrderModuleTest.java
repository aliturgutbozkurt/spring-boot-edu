package com.springbootedu.modulith.order;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
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
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

/**
 * Lesson 3.5 — only the order module is started; its dependency (catalog) is mocked.
 */
// tag::module-test[]
@ApplicationModuleTest                            // STANDALONE: the beans of this module only
@Import(TestcontainersConfiguration.class)
@TestPropertySource(properties = "bookstore.tour.enabled=false")    // the tour needs all modules
class OrderModuleTest {

    @MockitoBean
    CatalogService catalog;                       // another module: replaced by a mock

    @Autowired
    OrderService orders;

    @Test
    void placingAnOrderPublishesOrderPlaced(Scenario scenario) {
        given(catalog.find("9780134685991"))
                .willReturn(Optional.of(new Book("9780134685991", "Effective Java", new BigDecimal("89.90"))));

        scenario.stimulate(() -> orders.place("c-1", "9780134685991", 2))
                .andWaitForEventOfType(OrderPlaced.class)
                .matching(event -> event.customerId().equals("c-1"))
                .toArriveAndVerify(event -> assertThat(event.total()).isEqualByComparingTo("179.80"));
    }
    // end::module-test[]

    @Test
    void anUnknownBookIsRejected() {
        given(catalog.find("0000000000000")).willReturn(Optional.empty());

        assertThatThrownBy(() -> orders.place("c-1", "0000000000000", 1))
                .isInstanceOf(UnknownBookException.class);
    }
}
