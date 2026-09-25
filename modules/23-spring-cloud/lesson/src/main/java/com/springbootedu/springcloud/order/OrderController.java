package com.springbootedu.springcloud.order;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Lesson 3.4 — POST /api/orders?client=http|feign
 */
@RestController
class OrderController {

    private final OrderService orders;

    OrderController(OrderService orders) {
        this.orders = orders;
    }

    @PostMapping("/api/orders")
    OrderResponse place(@Valid @RequestBody OrderRequest request, @RequestParam(defaultValue = "http") String client) {
        return orders.place(request, client);
    }

    @ExceptionHandler
    ProblemDetail tooMany(IllegalArgumentException e) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.getMessage());
    }
}
