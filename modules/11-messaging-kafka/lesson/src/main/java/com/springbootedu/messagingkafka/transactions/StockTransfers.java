package com.springbootedu.messagingkafka.transactions;

import jakarta.annotation.PreDestroy;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.stereotype.Component;

/**
 * Lesson 3.5 — moving stock between warehouses is two messages. Consumers must see both, or neither.
 */
// tag::kafka-transaction[]
@Component
public class StockTransfers {

    public static final String TOPIC = "stock-transfers";

    private final DefaultKafkaProducerFactory<String, String> transactionalProducers;
    private final KafkaTemplate<String, String> kafka;

    public StockTransfers(ProducerFactory<Object, Object> producers) {
        this.transactionalProducers = new DefaultKafkaProducerFactory<>(producers.getConfigurationProperties(),
                new StringSerializer(), new StringSerializer());
        transactionalProducers.setTransactionIdPrefix("stock-transfer-");   // makes the producers transactional
        this.kafka = new KafkaTemplate<>(transactionalProducers);
    }

    public void transfer(String transferId, String from, String to, int quantity, boolean failInTheMiddle) {
        kafka.executeInTransaction(operations -> {
            operations.send(TOPIC, transferId, "withdraw " + quantity + " from " + from);
            operations.flush();                                      // really written to the log …
            if (failInTheMiddle) {
                throw new IllegalStateException("failure after the first message");   // … and then aborted
            }
            operations.send(TOPIC, transferId, "deposit " + quantity + " to " + to);
            return null;
        });
    }
    // end::kafka-transaction[]

    @PreDestroy
    void close() {
        transactionalProducers.destroy();
    }
}
