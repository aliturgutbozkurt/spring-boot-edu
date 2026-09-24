package com.springbootedu.testing.order;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.springbootedu.testing.TestcontainersConfiguration;
import com.springbootedu.testing.audit.AuditLog;
import com.springbootedu.testing.book.BookRepository;
import com.springbootedu.testing.payment.PaymentClient;
import com.springbootedu.testing.payment.PaymentReceipt;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.web.servlet.client.RestTestClient;
import org.springframework.web.client.RestClientException;

/**
 * Lesson 3.6 — an integration test: the whole application on a real port, a real database, only the external
 * payment service is a mock. The top of the pyramid: slower, but it proves that the parts fit together.
 */
// tag::integration[]
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureRestTestClient
@Import(TestcontainersConfiguration.class)
class OrderFlowIT {

    @Autowired
    RestTestClient http;

    @Autowired
    BookRepository books;

    @MockitoBean                                    // the external system: never called for real in tests
    PaymentClient payments;

    @MockitoSpyBean                                 // the real bean, but its calls can be verified
    AuditLog audit;

    @Test
    void anOrderIsPaidStoredAndAudited() {
        given(payments.charge(any())).willReturn(new PaymentReceipt("pay-42", "PAID"));
        int stockBefore = stockOf("9780134685991");

        http.post().uri("/api/orders").contentType(MediaType.APPLICATION_JSON)
                .body("""
                        {"isbn": "9780134685991", "quantity": 2}""")
                .exchange()
                .expectStatus().isCreated()
                .expectBody().jsonPath("$.paymentId").isEqualTo("pay-42");

        assertThat(stockOf("9780134685991")).isEqualTo(stockBefore - 2);
        then(audit).should().record("ordered 2 × 9780134685991 for 179.80");
    }

    @Test
    void aFailedPaymentRollsTheStockBack() {
        given(payments.charge(any())).willThrow(new RestClientException("payment service down"));
        int stockBefore = stockOf("9781617297571");

        http.post().uri("/api/orders").contentType(MediaType.APPLICATION_JSON)
                .body("""
                        {"isbn": "9781617297571", "quantity": 1}""")
                .exchange()
                .expectStatus().is5xxServerError();

        assertThat(stockOf("9781617297571")).isEqualTo(stockBefore);   // @Transactional rolled back
    }
    // end::integration[]

    private int stockOf(String isbn) {
        return books.findByIsbn(isbn).orElseThrow().getStock();
    }
}
