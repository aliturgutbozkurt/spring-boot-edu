package com.springbootedu.httpclientsresilience;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = "bookstore.tour.enabled=false")   // the tour calls the fake catalog over a real port
class HttpClientsResilienceApplicationTest {

    @Test
    void contextLoads() {
    }
}
