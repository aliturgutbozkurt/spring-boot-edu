package com.springbootedu.datajdbcpostgres;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@SpringBootTest(properties = "bookstore.tour.enabled=false")
@Import(TestcontainersConfiguration.class)
class DataJdbcPostgresApplicationTest {

    @Test
    void contextLoads() {
    }
}
