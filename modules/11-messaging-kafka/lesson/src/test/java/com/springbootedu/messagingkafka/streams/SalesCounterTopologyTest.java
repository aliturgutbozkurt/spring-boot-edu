package com.springbootedu.messagingkafka.streams;

import static org.assertj.core.api.Assertions.assertThat;

import com.springbootedu.messagingkafka.order.OrderPlaced;
import java.util.Properties;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.TestInputTopic;
import org.apache.kafka.streams.TestOutputTopic;
import org.apache.kafka.streams.TopologyTestDriver;
import org.apache.kafka.streams.state.KeyValueStore;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.support.serializer.JacksonJsonSerde;

/**
 * Lesson 3.7 — a Kafka Streams topology tested without any broker, with {@link TopologyTestDriver}.
 */
class SalesCounterTopologyTest {

    TopologyTestDriver driver;
    TestInputTopic<String, OrderPlaced> orders;
    TestOutputTopic<String, Long> salesPerBook;

    @BeforeEach
    void setUp() {
        StreamsBuilder builder = new StreamsBuilder();
        new SalesCounter().salesPerBook(builder);

        Properties config = new Properties();
        config.put(StreamsConfig.APPLICATION_ID_CONFIG, "test");
        config.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "unused:9092");
        driver = new TopologyTestDriver(builder.build(), config);

        orders = driver.createInputTopic("orders", Serdes.String().serializer(),
                new JacksonJsonSerde<>(OrderPlaced.class).serializer());
        salesPerBook = driver.createOutputTopic(SalesCounter.OUTPUT_TOPIC, Serdes.String().deserializer(),
                Serdes.Long().deserializer());
    }

    @AfterEach
    void tearDown() {
        driver.close();
    }

    @Test
    void countsTheSoldCopiesPerBook() {
        orders.pipeInput("o-1", new OrderPlaced("o-1", "c-1", "9780134685991", 2));
        orders.pipeInput("o-2", new OrderPlaced("o-2", "c-2", "9781617297571", 1));
        orders.pipeInput("o-3", new OrderPlaced("o-3", "c-3", "9780134685991", 3));

        KeyValueStore<String, Long> store = driver.getKeyValueStore(SalesCounter.STORE);
        assertThat(store.get("9780134685991")).isEqualTo(5L);
        assertThat(store.get("9781617297571")).isEqualTo(1L);
    }

    @Test
    void everyChangeOfACountIsPublished() {
        orders.pipeInput("o-1", new OrderPlaced("o-1", "c-1", "9780134685991", 2));
        orders.pipeInput("o-2", new OrderPlaced("o-2", "c-1", "9780134685991", 1));

        assertThat(salesPerBook.readKeyValuesToMap()).containsEntry("9780134685991", 3L);
    }

    @Test
    void invalidOrdersAreNotCounted() {
        orders.pipeInput("o-1", new OrderPlaced("o-1", "c-1", "9780134685991", 0));

        assertThat(salesPerBook.isEmpty()).isTrue();
    }
}
