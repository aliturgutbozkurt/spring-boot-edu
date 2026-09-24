package com.springbootedu.testing.order;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;

import com.springbootedu.testing.book.BookNotFoundException;
import com.springbootedu.testing.book.OutOfStockException;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;

/**
 * Lesson 3.3 — a web slice test: only the MVC layer is started; the service is a Mockito bean.
 */
// tag::web-slice[]
@WebMvcTest(OrderController.class)
class OrderControllerTest {

    @Autowired
    MockMvcTester mvc;

    @MockitoBean                                   // replaces the bean in the Spring context with a mock
    OrderService orders;

    @Test
    void aValidOrderIsCreated() {
        given(orders.placeOrder("9780134685991", 2)).willReturn(new OrderConfirmation(
                "9780134685991", "Effective Java", 2, new BigDecimal("179.80"), "pay-7"));

        assertThat(post("""
                {"isbn": "9780134685991", "quantity": 2}"""))
                .hasStatus(201)
                .hasHeader("Location", "/api/orders/pay-7")
                .bodyJson().extractingPath("$.total").isEqualTo("179.80");
    }

    @Test
    void anInvalidBodyIs400AndNeverReachesTheService() {
        assertThat(post("""
                {"isbn": "", "quantity": 0}""")).hasStatus(400);
        then(orders).should(never()).placeOrder(anyString(), anyInt());
    }
    // end::web-slice[]

    @Test
    void anUnknownBookIs404WithAProblemDetail() {
        given(orders.placeOrder("unknown", 1)).willThrow(new BookNotFoundException("unknown"));

        assertThat(post("""
                {"isbn": "unknown", "quantity": 1}"""))
                .hasStatus(404)
                .bodyJson().extractingPath("$.detail").isEqualTo("No book with ISBN unknown");
    }

    @Test
    void notEnoughStockIs409() {
        given(orders.placeOrder("9780321336781", 1)).willThrow(new OutOfStockException("9780321336781", 1, 0));

        assertThat(post("""
                {"isbn": "9780321336781", "quantity": 1}""")).hasStatus(409);
    }

    private MockMvcTester.MockMvcRequestBuilder post(String json) {
        return mvc.post().uri("/api/orders").contentType(MediaType.APPLICATION_JSON).content(json);
    }
}
