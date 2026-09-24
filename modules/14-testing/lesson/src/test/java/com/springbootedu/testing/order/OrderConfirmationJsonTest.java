package com.springbootedu.testing.order;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

/**
 * Lesson 3.5 — a JSON slice test: only Jackson is configured, exactly as the application uses it.
 */
// tag::json-slice[]
@JsonTest
class OrderConfirmationJsonTest {

    @Autowired
    JacksonTester<OrderConfirmation> json;

    @Test
    void writesMoneyAsAString() throws Exception {
        var confirmation = new OrderConfirmation("9780134685991", "Effective Java", 2,
                new BigDecimal("179.80"), "pay-7");

        assertThat(json.write(confirmation))
                .extractingJsonPathStringValue("$.total").isEqualTo("179.80");   // not 179.8
    }

    @Test
    void readsTheSameFormatBack() throws Exception {
        var read = json.parseObject("""
                {"isbn":"9780134685991","title":"Effective Java","quantity":2,"total":"179.80","paymentId":"pay-7"}""");

        assertThat(read.total()).isEqualByComparingTo("179.80");
    }
}
// end::json-slice[]
