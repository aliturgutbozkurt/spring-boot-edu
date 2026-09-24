package com.springbootedu.security;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

/**
 * The whole application starts on a real port, and the lesson tour runs its HTTP calls against it.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestcontainersConfiguration.class)
class SecurityApplicationTest {

    @Test
    void contextLoads() {
    }
}
