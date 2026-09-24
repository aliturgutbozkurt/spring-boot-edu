package com.springbootedu.reactive;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

/**
 * The whole application starts, and the lesson tour runs against real PostgreSQL and MongoDB.
 */
@SpringBootTest
@Import(TestcontainersConfiguration.class)
class ReactiveApplicationTest {

    @Test
    void contextLoads() {
    }
}
