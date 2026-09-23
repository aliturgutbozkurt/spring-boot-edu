package com.springbootedu.datamongodb.orders;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.springbootedu.datamongodb.TestcontainersConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.core.MongoTemplate;

/**
 * Lesson 3.6 — a multi-document transaction: the order and the stock change commit together.
 */
@SpringBootTest(properties = "bookstore.tour.enabled=false")
@Import(TestcontainersConfiguration.class)
class OrderServiceTest {

    @Autowired
    OrderService orders;

    @Autowired
    MongoTemplate mongo;

    @BeforeEach
    void seed() {
        mongo.dropCollection(Order.class);
        mongo.dropCollection(Inventory.class);
        mongo.createCollection(Order.class);                  // collections must exist before a transaction uses them
        mongo.save(new Inventory("9780134685991", 2));
    }

    private int stock() {
        return mongo.findById("9780134685991", Inventory.class).quantity();
    }

    @Test
    void successCommitsBothDocuments() {
        orders.place("ayse@example.com", "9780134685991", 2);

        assertThat(stock()).isZero();
        assertThat(mongo.count(new org.springframework.data.mongodb.core.query.Query(), Order.class)).isEqualTo(1);
    }

    @Test
    void failureRollsBackTheOrderInsertedBeforeIt() {
        assertThatThrownBy(() -> orders.place("ayse@example.com", "9780134685991", 5))
                .isInstanceOf(IllegalStateException.class);

        assertThat(stock()).isEqualTo(2);
        assertThat(mongo.count(new org.springframework.data.mongodb.core.query.Query(), Order.class)).isZero();
    }
}
