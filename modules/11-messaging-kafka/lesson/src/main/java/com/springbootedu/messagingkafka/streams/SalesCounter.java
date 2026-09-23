package com.springbootedu.messagingkafka.streams;

import com.springbootedu.messagingkafka.order.OrderEvents;
import com.springbootedu.messagingkafka.order.OrderPlaced;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Grouped;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.streams.state.KeyValueStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafkaStreams;
import org.springframework.kafka.support.serializer.JacksonJsonSerde;

/**
 * Lesson 3.7 — a stream processing topology: sold copies per book, updated with every order event.
 */
// tag::streams[]
@Configuration(proxyBeanMethods = false)
@EnableKafkaStreams
public class SalesCounter {

    public static final String STORE = "sales-per-book";
    public static final String OUTPUT_TOPIC = "sales-per-book";

    @Bean
    public KStream<String, OrderPlaced> salesPerBook(StreamsBuilder builder) {
        var orderSerde = new JacksonJsonSerde<>(OrderPlaced.class).ignoreTypeHeaders();

        KStream<String, OrderPlaced> orders =
                builder.stream(OrderEvents.TOPIC, Consumed.with(Serdes.String(), orderSerde));
        orders.filter((orderId, order) -> order != null && order.quantity() > 0)
                .groupBy((orderId, order) -> order.isbn(), Grouped.with(Serdes.String(), orderSerde))   // re-key
                .aggregate(() -> 0L, (isbn, order, total) -> total + order.quantity(),
                        Materialized.<String, Long, KeyValueStore<Bytes, byte[]>>as(STORE)             // local state
                                .withKeySerde(Serdes.String()).withValueSerde(Serdes.Long()))
                .toStream()
                .to(OUTPUT_TOPIC, Produced.with(Serdes.String(), Serdes.Long()));                  // every update
        return orders;
    }
}
// end::streams[]
