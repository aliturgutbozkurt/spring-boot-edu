package com.springbootedu.messagingkafka;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

/**
 * The whole application starts, and the lesson tour runs against real Kafka and PostgreSQL.
 */
@SpringBootTest
@Import(TestcontainersConfiguration.class)
class MessagingKafkaApplicationTest {

    @Test
    void contextLoads() {
    }
}
