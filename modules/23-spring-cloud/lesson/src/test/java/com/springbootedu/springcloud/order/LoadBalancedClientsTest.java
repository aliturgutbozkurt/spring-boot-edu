package com.springbootedu.springcloud.order;

import static org.assertj.core.api.Assertions.assertThat;

import com.springbootedu.springcloud.CatalogInstances;
import com.springbootedu.springcloud.catalog.Book;
import com.springbootedu.springcloud.catalog.CatalogFeignClient;
import com.springbootedu.springcloud.catalog.CatalogHttpClient;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

/**
 * Lessons 3.2–3.3 — both clients reach the service by its name, and the load balancer uses both instances.
 */
@SpringBootTest
class LoadBalancedClientsTest {

    @DynamicPropertySource
    static void catalog(DynamicPropertyRegistry registry) {
        CatalogInstances.register(registry);
    }

    @Autowired
    CatalogHttpClient httpClient;

    @Autowired
    CatalogFeignClient feignClient;

    @BeforeEach
    void healthyCatalog() {
        CatalogInstances.healthy();
    }

    @Test
    void theHttpInterfaceAndFeignGiveTheSameBook() {
        Book viaHttpInterface = httpClient.find("9780134685991");
        Book viaFeign = feignClient.find("9780134685991");

        assertThat(viaHttpInterface.title()).isEqualTo(viaFeign.title()).isEqualTo("Effective Java");
    }

    @Test
    void theLoadBalancerUsesBothInstances() {
        Set<String> servedBy = new HashSet<>();
        for (int i = 0; i < 4; i++) {
            servedBy.add(httpClient.find("9780134685991").servedBy());
        }

        assertThat(servedBy).containsExactlyInAnyOrder("catalog-1", "catalog-2");   // round robin
    }
}
