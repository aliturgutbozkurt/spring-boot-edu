package com.springbootedu.capstone.order.exercises;

import static com.springbootedu.capstone.order.FakeStockService.EFFECTIVE_JAVA;
import static org.assertj.core.api.Assertions.assertThat;

import com.springbootedu.capstone.order.FakeStockService;
import com.springbootedu.capstone.order.OrderTest;
import com.springbootedu.capstone.order.Tokens;
import com.springbootedu.capstone.order.TopicReader;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.kafka.autoconfigure.KafkaConnectionDetails;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.test.web.servlet.assertj.MvcTestResult;

/**
 * Capstone exercise 2 — DELETE /api/orders/{id} cancels an order: the catalog gets the stock back (gRPC
 * ReleaseStock), the order is marked CANCELLED, and OrderCancelled goes through the outbox to Kafka.
 */
@OrderTest
class Exercise2CancellationTest {

    static final FakeStockService STOCK = OrderTest.FakeCatalog.STOCK;
    static final String CANCELLATIONS = "bookstore.order-cancellations";

    @Autowired
    MockMvcTester mvc;

    @Autowired
    KafkaConnectionDetails kafka;

    String customer = "customer-" + UUID.randomUUID();

    @BeforeEach
    void resetTheCatalog() {
        STOCK.reset();
    }

    @Test
    void aCancelledOrderGivesTheStockBack() {
        String id = placeOrder();

        assertThat(cancel(id, customer)).hasStatus(HttpStatus.NO_CONTENT);

        assertThat(STOCK.released).containsExactly(id);
        assertThat(mvc.get().uri("/api/orders/" + id).header(HttpHeaders.AUTHORIZATION, Tokens.bearer(customer, "USER")))
                .hasStatusOk().bodyJson().extractingPath("$.status").isEqualTo("CANCELLED");
    }

    @Test
    void theCancellationIsPublished() {
        String id = placeOrder();
        assertThat(cancel(id, customer)).hasStatus(HttpStatus.NO_CONTENT);

        assertThat(TopicReader.valuesWithKey(kafka, CANCELLATIONS, id)).singleElement().asString()
                .contains("\"orderId\":\"" + id + "\"", "\"isbn\":\"" + EFFECTIVE_JAVA + "\"");
    }

    @Test
    void anOrderIsCancelledOnlyOnce() {
        String id = placeOrder();
        assertThat(cancel(id, customer)).hasStatus(HttpStatus.NO_CONTENT);

        assertThat(cancel(id, customer)).hasStatus(HttpStatus.CONFLICT);
        assertThat(STOCK.released).hasSize(1);
    }

    @Test
    void nobodyCancelsTheOrderOfSomeoneElse() {
        String id = placeOrder();

        assertThat(cancel(id, "intruder-" + UUID.randomUUID())).hasStatus(HttpStatus.NOT_FOUND);
        assertThat(STOCK.released).isEmpty();
    }

    private String placeOrder() {
        MvcTestResult result = mvc.post().uri("/api/orders").header(HttpHeaders.AUTHORIZATION, Tokens.bearer(customer, "USER"))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"lines\":[{\"isbn\":\"" + EFFECTIVE_JAVA + "\",\"quantity\":2}]}").exchange();
        assertThat(result).hasStatus(HttpStatus.CREATED);
        String location = result.getResponse().getHeader(HttpHeaders.LOCATION);
        return location.substring(location.lastIndexOf('/') + 1);
    }

    private MvcTestResult cancel(String id, String customerId) {
        return mvc.delete().uri("/api/orders/" + id).header(HttpHeaders.AUTHORIZATION, Tokens.bearer(customerId, "USER"))
                .exchange();
    }
}
