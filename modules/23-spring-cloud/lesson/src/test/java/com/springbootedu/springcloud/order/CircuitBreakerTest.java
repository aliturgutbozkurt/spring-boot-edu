package com.springbootedu.springcloud.order;

import static org.assertj.core.api.Assertions.assertThat;

import com.springbootedu.springcloud.CatalogInstances;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

/**
 * Lesson 3.4 — failures open the breaker; then the fallback answers at once, without calling the catalog.
 */
@SpringBootTest
class CircuitBreakerTest {

    @DynamicPropertySource
    static void catalog(DynamicPropertyRegistry registry) {
        CatalogInstances.register(registry);
    }

    @Autowired
    OrderService orders;

    @Autowired
    CircuitBreakerRegistry breakers;

    @BeforeEach
    void closedBreaker() {
        breakers.circuitBreaker("catalog").reset();
    }

    @Test
    void aHealthyCatalogPricesTheOrder() {
        CatalogInstances.healthy();

        OrderResponse order = orders.place(new OrderRequest("9780134685991", 2), "feign");

        assertThat(order.status()).isEqualTo(OrderResponse.Status.PRICED);
        assertThat(order.total()).isEqualByComparingTo("179.80");
    }

    // tag::breaker-test[]
    @Test
    void failuresOpenTheBreakerAndTheFallbackAnswers() {
        CatalogInstances.failing();

        for (int i = 0; i < 4; i++) {                         // minimum-number-of-calls: 4
            assertThat(orders.place(new OrderRequest("9780134685991", 1), "http").status())
                    .isEqualTo(OrderResponse.Status.PENDING);
        }

        assertThat(breakers.circuitBreaker("catalog").getState()).isEqualTo(CircuitBreaker.State.OPEN);
        int callsBefore = CatalogInstances.CATALOG_1.getAllServeEvents().size()
                          + CatalogInstances.CATALOG_2.getAllServeEvents().size();
        orders.place(new OrderRequest("9780134685991", 1), "http");
        int callsAfter = CatalogInstances.CATALOG_1.getAllServeEvents().size()
                         + CatalogInstances.CATALOG_2.getAllServeEvents().size();
        assertThat(callsAfter).isEqualTo(callsBefore);        // open: the catalog is not called at all
    }
    // end::breaker-test[]
}
