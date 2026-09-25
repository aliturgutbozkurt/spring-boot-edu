package com.springbootedu.capstone.order.exercises;

import static com.springbootedu.capstone.order.FakeStockService.DOWN;
import static com.springbootedu.capstone.order.FakeStockService.EFFECTIVE_JAVA;
import static com.springbootedu.capstone.order.FakeStockService.FLAKY;
import static org.assertj.core.api.Assertions.assertThat;

import com.springbootedu.capstone.order.FakeStockService;
import com.springbootedu.capstone.order.OrderTest;
import com.springbootedu.capstone.order.Tokens;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.test.web.servlet.assertj.MvcTestResult;

/**
 * Capstone exercise 3 — a catalog restart (gRPC UNAVAILABLE for a moment) must not fail the order: the reservation
 * is retried. That is safe only because ReserveStock is idempotent per order ID. "Not enough stock" is never retried.
 */
@OrderTest
class Exercise3RetryTest {

    static final FakeStockService STOCK = OrderTest.FakeCatalog.STOCK;

    @Autowired
    MockMvcTester mvc;

    String customer = "customer-" + UUID.randomUUID();

    @BeforeEach
    void resetTheCatalog() {
        STOCK.reset();
    }

    @Test
    void aShortOutageIsBridged() {
        assertThat(place(FLAKY, 1)).hasStatus(HttpStatus.CREATED);         // fails twice, then answers

        assertThat(STOCK.attempts.values()).singleElement().extracting(AtomicInteger::get).isEqualTo(3);
    }

    @Test
    void aLongOutageStillEndsInServiceUnavailable() {
        assertThat(place(DOWN, 1)).hasStatus(HttpStatus.SERVICE_UNAVAILABLE);

        assertThat(STOCK.attempts.values()).singleElement().extracting(AtomicInteger::get).isEqualTo(3);
    }

    @Test
    void notEnoughStockIsNotRetried() {
        assertThat(place(EFFECTIVE_JAVA, 6)).hasStatus(HttpStatus.CONFLICT);

        assertThat(STOCK.attempts.values()).singleElement().extracting(AtomicInteger::get).isEqualTo(1);
    }

    private MvcTestResult place(String isbn, int quantity) {
        return mvc.post().uri("/api/orders").header(HttpHeaders.AUTHORIZATION, Tokens.bearer(customer, "USER"))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"lines\":[{\"isbn\":\"" + isbn + "\",\"quantity\":" + quantity + "}]}").exchange();
    }
}
