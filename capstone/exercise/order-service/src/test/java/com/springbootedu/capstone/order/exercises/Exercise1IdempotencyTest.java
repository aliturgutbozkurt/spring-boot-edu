package com.springbootedu.capstone.order.exercises;

import static com.springbootedu.capstone.order.FakeStockService.EFFECTIVE_JAVA;
import static org.assertj.core.api.Assertions.assertThat;

import com.springbootedu.capstone.order.FakeStockService;
import com.springbootedu.capstone.order.OrderTest;
import com.springbootedu.capstone.order.Tokens;
import java.util.UUID;
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.test.web.servlet.assertj.MvcTestResult;

/**
 * Capstone exercise 1 — a client that retries POST /api/orders (timeout, lost answer) must not order twice.
 * It sends an Idempotency-Key header; the same key of the same customer gives back the first order.
 */
@OrderTest
class Exercise1IdempotencyTest {

    static final FakeStockService STOCK = OrderTest.FakeCatalog.STOCK;

    @Autowired
    MockMvcTester mvc;

    @Autowired
    JdbcClient jdbc;

    String customer = "customer-" + UUID.randomUUID();

    @BeforeEach
    void resetTheCatalog() {
        STOCK.reset();
    }

    @Test
    void theSameKeyGivesTheSameOrder() {
        MvcTestResult first = place(customer, "key-1");
        MvcTestResult retry = place(customer, "key-1");

        assertThat(first).hasStatus(HttpStatus.CREATED);
        assertThat(retry).hasStatus(HttpStatus.OK);                         // not created again
        assertThat(retry).bodyJson().extractingPath("$.id").isEqualTo(idOf(first));
        assertThat(ordersOf(customer)).isEqualTo(1);
        assertThat(STOCK.reserved).hasSize(1);                             // the stock was reserved once
    }

    @Test
    void aKeyBelongsToOneCustomer() {
        String other = "other-" + UUID.randomUUID();

        assertThat(place(customer, "shared-key")).hasStatus(HttpStatus.CREATED);
        assertThat(place(other, "shared-key")).hasStatus(HttpStatus.CREATED);
        assertThat(ordersOf(other)).isEqualTo(1);
    }

    @Test
    void withoutAKeyEveryRequestIsANewOrder() {
        assertThat(place(customer, null)).hasStatus(HttpStatus.CREATED);
        assertThat(place(customer, null)).hasStatus(HttpStatus.CREATED);
        assertThat(ordersOf(customer)).isEqualTo(2);
    }

    private MvcTestResult place(String customerId, @Nullable String key) {
        var request = mvc.post().uri("/api/orders").header(HttpHeaders.AUTHORIZATION, Tokens.bearer(customerId, "USER"))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"lines\":[{\"isbn\":\"" + EFFECTIVE_JAVA + "\",\"quantity\":1}]}");
        if (key != null) {
            request.header("Idempotency-Key", key);
        }
        return request.exchange();
    }

    private static String idOf(MvcTestResult result) {
        String location = result.getResponse().getHeader(HttpHeaders.LOCATION);
        return location.substring(location.lastIndexOf('/') + 1);
    }

    private int ordersOf(String customerId) {
        return jdbc.sql("SELECT count(*) FROM orders WHERE customer_id = ?").param(customerId).query(Integer.class).single();
    }
}
