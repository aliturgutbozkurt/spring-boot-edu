package com.springbootedu.messagingkafka;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@SpringBootTest
@Import(TestcontainersConfiguration.class)
class MessagingKafkaApplicationTest {

    @Test
    void contextLoads() {
    }
}
