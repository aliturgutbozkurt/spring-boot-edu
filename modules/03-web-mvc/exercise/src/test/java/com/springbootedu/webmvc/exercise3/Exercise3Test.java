package com.springbootedu.webmvc.exercise3;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.assertj.MockMvcTester;

/**
 * Exercise 3 — two API versions of an order summary, selected with the API-Version header.
 */
@WebMvcTest(OrderSummaryController.class)
class Exercise3Test {

    @Autowired
    MockMvcTester mvc;

    @Test
    void version1IsFlat() {
        assertThat(mvc.get().uri("/api/order-summaries/1001").header("API-Version", "1"))
                .hasStatusOk()
                .bodyJson().isLenientlyEqualTo("""
                        {"id": 1001, "total": 145.00, "status": "PAID"}""");
    }

    @Test
    void version2HasMoneyStatusLabelAndLines() {
        assertThat(mvc.get().uri("/api/order-summaries/1001").header("API-Version", "2"))
                .hasStatusOk()
                .bodyJson().isLenientlyEqualTo("""
                        {"id": 1001,
                         "total": {"amount": 145.00, "currency": "TRY"},
                         "status": {"code": "PAID", "label": "Ödendi / Paid"},
                         "lines": [{"title": "Effective Java", "quantity": 1}, {"title": "Java Puzzlers", "quantity": 1}]}""");
    }

    @Test
    void withoutAHeaderVersion1IsUsed() {
        assertThat(mvc.get().uri("/api/order-summaries/1001"))
                .hasStatusOk()
                .bodyJson().extractingPath("$.status").isEqualTo("PAID");
    }

    @Test
    void anUnsupportedVersionIsRejected() {
        assertThat(mvc.get().uri("/api/order-summaries/1001").header("API-Version", "3"))
                .hasStatus(HttpStatus.BAD_REQUEST);
    }
}
