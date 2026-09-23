package com.springbootedu.messagingkafka.exercise3;

import static org.assertj.core.api.Assertions.assertThat;

import com.springbootedu.messagingkafka.KafkaTestSupport;
import com.springbootedu.messagingkafka.TestcontainersConfiguration;
import java.time.Duration;
import java.util.List;
import java.util.UUID;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.kafka.autoconfigure.KafkaConnectionDetails;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.simple.JdbcClient;

/**
 * Exercise 3 — the outbox relay: every stored event reaches Kafka once, in the order it was written.
 */
@SpringBootTest
@Import(TestcontainersConfiguration.class)
class Exercise3Test {

    @Autowired
    OrderDesk desk;

    @Autowired
    OutboxRelay relay;

    @Autowired
    JdbcClient jdbc;

    @Autowired
    KafkaConnectionDetails connection;

    @BeforeEach
    void emptyOutbox() {
        jdbc.sql("DELETE FROM outbox").update();
    }

    @Test
    void theRelaySendsEveryUnsentEventInOrderAndMarksItSent() {
        String customer = UUID.randomUUID().toString();
        List<String> ids = List.of(desk.place(customer, 1), desk.place(customer, 2), desk.place(customer, 3));

        assertThat(relay.relayBatch(10)).isEqualTo(3);

        assertThat(keysOnTopic(ids)).containsExactlyElementsOf(ids);      // same order as in the outbox
        assertThat(jdbc.sql("SELECT count(*) FROM outbox WHERE sent_at IS NULL").query(Long.class).single()).isZero();
    }

    @Test
    void aSecondRunSendsNothingAgain() {
        desk.place(UUID.randomUUID().toString(), 1);

        assertThat(relay.relayBatch(10)).isEqualTo(1);
        assertThat(relay.relayBatch(10)).isZero();
    }

    @Test
    void oneRunSendsAtMostTheBatchSize() {
        String customer = UUID.randomUUID().toString();
        for (int i = 1; i <= 5; i++) {
            desk.place(customer, i);
        }

        assertThat(relay.relayBatch(2)).isEqualTo(2);
        assertThat(relay.relayBatch(2)).isEqualTo(2);
        assertThat(relay.relayBatch(2)).isEqualTo(1);
    }

    private List<String> keysOnTopic(List<String> ids) {
        return KafkaTestSupport.readAll(connection, OutboxRelay.TOPIC, "read_uncommitted", Duration.ofSeconds(3))
                .stream().map(ConsumerRecord::key).filter(ids::contains).toList();
    }
}
