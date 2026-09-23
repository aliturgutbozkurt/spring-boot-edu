package com.springbootedu.webmvc.exercise2;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.assertj.MockMvcTester;

/**
 * Exercise 2 — domain errors as ProblemDetail.
 */
@WebMvcTest(OrderController.class)
@Import({StockService.class, OrderProblemHandler.class})
class Exercise2Test {

    @Autowired
    MockMvcTester mvc;

    @Test
    void aSuccessfulOrderIsCreated() {
        assertThat(order("9780134685991", 2)).hasStatus(HttpStatus.CREATED)
                .bodyJson().isLenientlyEqualTo("""
                        {"isbn": "9780134685991", "quantity": 2, "remainingStock": 3}""");
    }

    @Test
    void notEnoughStockIsA409ProblemWithDetails() {
        assertThat(order("9780321336781", 5))
                .hasStatus(HttpStatus.CONFLICT)
                .hasContentType(MediaType.APPLICATION_PROBLEM_JSON)
                .bodyJson().isLenientlyEqualTo("""
                        {"type": "https://springbootedu.com/problems/out-of-stock", "title": "Out of stock",
                         "status": 409, "detail": "Only 1 of 5 requested copies are in stock",
                         "instance": "/api/orders",
                         "isbn": "9780321336781", "requested": 5, "available": 1}""");
    }

    @Test
    void anUnknownBookIsA404Problem() {
        assertThat(order("9780000000000", 1))
                .hasStatus(HttpStatus.NOT_FOUND)
                .hasContentType(MediaType.APPLICATION_PROBLEM_JSON)
                .bodyJson().isLenientlyEqualTo("""
                        {"type": "https://springbootedu.com/problems/book-not-found", "title": "Book not found",
                         "status": 404, "isbn": "9780000000000"}""");
    }

    private org.springframework.test.web.servlet.assertj.MvcTestResult order(String isbn, int quantity) {
        return mvc.post().uri("/api/orders").contentType(MediaType.APPLICATION_JSON)
                .content("{\"isbn\": \"%s\", \"quantity\": %d}".formatted(isbn, quantity))
                .exchange();
    }
}
