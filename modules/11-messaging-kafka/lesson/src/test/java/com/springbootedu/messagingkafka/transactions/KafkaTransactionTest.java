package com.springbootedu.messagingkafka.transactions;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.springbootedu.messagingkafka.KafkaTestSupport;
import com.springbootedu.messagingkafka.TestcontainersConfiguration;
import java.time.Duration;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.kafka.autoconfigure.KafkaConnectionDetails;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

/**
 * Lesson 3.5 — several messages written in one Kafka transaction become visible together, or not at all.
 */
@SpringBootTest(properties = "bookstore.tour.enabled=false")
@Import(TestcontainersConfiguration.class)
class KafkaTransactionTest {

    @Autowired
    StockTransfers transfers;

    @Autowired
    KafkaConnectionDetails kafka;

    @Test
    void aCommittedTransactionIsVisibleAsAWhole() {
        String id = UUID.randomUUID().toString();

        transfers.transfer(id, "warehouse-a", "warehouse-b", 5, false);

        assertThat(readCommitted(id)).hasSize(2);
    }

    @Test
    void anAbortedTransactionIsInvisibleToReadCommittedConsumers() {
        String id = UUID.randomUUID().toString();

        assertThatThrownBy(() -> transfers.transfer(id, "warehouse-a", "warehouse-b", 5, true))
                .hasMessageContaining("failure after the first message");

        assertThat(readCommitted(id)).isEmpty();                        // the first message was written, then aborted
        assertThat(readUncommitted(id)).hasSize(1);                     // a read_uncommitted consumer still sees it
    }

    private List<String> readCommitted(String id) {
        return values(id, "read_committed");
    }

    private List<String> readUncommitted(String id) {
        return values(id, "read_uncommitted");
    }

    private List<String> values(String id, String isolation) {
        return KafkaTestSupport.readAll(kafka, StockTransfers.TOPIC, isolation, Duration.ofSeconds(3)).stream()
                .filter(record -> id.equals(record.key()))
                .map(record -> record.value())
                .toList();
    }
}
