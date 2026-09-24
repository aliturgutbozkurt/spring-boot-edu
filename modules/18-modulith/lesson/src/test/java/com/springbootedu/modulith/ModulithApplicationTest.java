package com.springbootedu.modulith;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@SpringBootTest(properties = "bookstore.tour.enabled=false")
@Import(TestcontainersConfiguration.class)
class ModulithApplicationTest {

    @Test
    void contextLoads() {
    }
}
