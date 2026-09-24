package com.springbootedu.testing.order;

import com.springbootedu.testing.book.BookNotFoundException;
import com.springbootedu.testing.book.OutOfStockException;
import jakarta.validation.Valid;
import java.net.URI;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Lesson 3.3 — HTTP in, HTTP out: validation, status codes and error bodies are what a web slice test checks.
 */
@RestController
@RequestMapping("/api/orders")
class OrderController {

    private final OrderService orders;

    OrderController(OrderService orders) {
        this.orders = orders;
    }

    @PostMapping
    ResponseEntity<OrderConfirmation> place(@Valid @RequestBody OrderRequest request) {
        OrderConfirmation confirmation = orders.placeOrder(request.isbn(), request.quantity());
        return ResponseEntity.created(URI.create("/api/orders/" + confirmation.paymentId())).body(confirmation);
    }

    @ExceptionHandler(BookNotFoundException.class)
    ProblemDetail notFound(BookNotFoundException e) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, e.getMessage());
    }

    @ExceptionHandler(OutOfStockException.class)
    ProblemDetail outOfStock(OutOfStockException e) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, e.getMessage());
    }
}
