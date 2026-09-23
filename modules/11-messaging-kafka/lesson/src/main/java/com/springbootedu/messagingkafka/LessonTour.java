package com.springbootedu.messagingkafka;

import com.springbootedu.messagingkafka.invoicing.Invoices;
import com.springbootedu.messagingkafka.order.OrderEvents;
import com.springbootedu.messagingkafka.order.OrderPlaced;
import com.springbootedu.messagingkafka.outbox.OrderService;
import com.springbootedu.messagingkafka.shipping.Shipments;
import com.springbootedu.messagingkafka.streams.SalesCounter;
import com.springbootedu.messagingkafka.transactions.StockTransfers;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StoreQueryParameters;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBooleanProperty;
import org.springframework.kafka.config.StreamsBuilderFactoryBean;
import org.springframework.stereotype.Component;

/**
 * Runs every example of the lesson once, in the order of the lesson notes (section 3).
 * Start it with: ./mvnw -pl modules/11-messaging-kafka/lesson spring-boot:run   (Docker must be running)
 */
@Component
@ConditionalOnBooleanProperty(name = "bookstore.tour.enabled", matchIfMissing = true)
class LessonTour implements ApplicationRunner {

    private final OrderEvents events;
    private final Shipments shipments;
    private final Invoices invoices;
    private final StockTransfers transfers;
    private final OrderService orders;
    private final StreamsBuilderFactoryBean streams;

    LessonTour(OrderEvents events, Shipments shipments, Invoices invoices, StockTransfers transfers,
               OrderService orders, StreamsBuilderFactoryBean streams) {
        this.events = events;
        this.shipments = shipments;
        this.invoices = invoices;
        this.transfers = transfers;
        this.orders = orders;
        this.streams = streams;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        section("3.1 Produce");
        var result = events.publish(order("customer-1", 2)).get(10, TimeUnit.SECONDS);
        print("sent to " + result.getRecordMetadata().topic() + "-" + result.getRecordMetadata().partition()
              + " at offset " + result.getRecordMetadata().offset());

        section("3.3 An invalid order (quantity 0) → orders.DLT");
        events.publish(order("customer-1", 0));

        section("3.4 A flaky invoice → retry topics");
        OrderPlaced flaky = order("flaky-customer", 1);
        events.publish(flaky);

        section("3.5 Kafka transaction");
        transfers.transfer(UUID.randomUUID().toString(), "warehouse-a", "warehouse-b", 5, false);
        print("committed: withdraw + deposit");

        section("3.6 Transactional outbox");
        print("placed: " + orders.place("customer-2", "9781617297571", 3).orderId() + " (the relay sends it)");

        Thread.sleep(5_000);                                            // let the consumers catch up

        section("3.2 Consume (group shipping, group invoicing)");
        print("shipped: " + shipments.shipped().size() + " order(s)");
        print("flaky order invoiced after " + invoices.attempts(flaky.orderId()) + " attempts");

        section("3.7 Kafka Streams");
        KafkaStreams kafkaStreams = streams.getKafkaStreams();
        for (int i = 0; i < 30 && kafkaStreams != null && kafkaStreams.state() != KafkaStreams.State.RUNNING; i++) {
            Thread.sleep(500);                                          // the first rebalance takes a moment
        }
        if (kafkaStreams != null && kafkaStreams.state() == KafkaStreams.State.RUNNING) {
            ReadOnlyKeyValueStore<String, Long> store = kafkaStreams.store(StoreQueryParameters.fromNameAndType(
                    SalesCounter.STORE, QueryableStoreTypes.keyValueStore()));
            print("sold copies of 9781617297571: " + store.get("9781617297571"));
        }
        print("(Ctrl+C stops the app)");
    }

    private static OrderPlaced order(String customerId, int quantity) {
        return new OrderPlaced(UUID.randomUUID().toString(), customerId, "9781617297571", quantity);
    }

    private static void section(String title) {
        System.out.println();
        System.out.println("== " + title);
    }

    private static void print(String line) {
        System.out.println(line);
    }
}
