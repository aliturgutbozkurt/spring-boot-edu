package com.springbootedu.graphqlwebsocket;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@SpringBootTest(properties = "bookstore.tour.enabled=false")
@Import(TestcontainersConfiguration.class)
class GraphqlWebsocketApplicationTest {

    @Test
    void contextLoads() {
    }
}
