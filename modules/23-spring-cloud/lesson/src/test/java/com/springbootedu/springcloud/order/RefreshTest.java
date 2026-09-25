package com.springbootedu.springcloud.order;

import static org.assertj.core.api.Assertions.assertThat;

import com.springbootedu.springcloud.CatalogInstances;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.context.refresh.ContextRefresher;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

/**
 * Lesson 3.5 — a changed value reaches a @RefreshScope bean without a restart (what POST /actuator/refresh does).
 */
@SpringBootTest
@DirtiesContext                                           // the test changes the environment
class RefreshTest {

    @DynamicPropertySource
    static void catalog(DynamicPropertyRegistry registry) {
        CatalogInstances.register(registry);
    }

    @Autowired
    OrderLimits limits;

    @Autowired
    ConfigurableEnvironment environment;

    @Autowired
    ContextRefresher refresher;

    @Test
    void aNewLimitWithoutARestart() {
        assertThat(limits.maxQuantity()).isEqualTo(10);

        environment.getPropertySources().addFirst(
                new MapPropertySource("changed-in-config-repo", Map.of("bookstore.order.max-quantity", 3)));
        refresher.refresh();                              // re-creates all @RefreshScope beans

        assertThat(limits.maxQuantity()).isEqualTo(3);
    }
}
