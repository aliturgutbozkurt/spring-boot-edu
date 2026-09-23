package com.springbootedu.webmvc.exercise2;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * Exercise 2 — given: places an order; the stock service throws domain exceptions.
 */
@RestController
public class OrderController {

    private final StockService stock;

    public OrderController(StockService stock) {
        this.stock = stock;
    }

    @PostMapping("/api/orders")
    @ResponseStatus(HttpStatus.CREATED)
    public OrderResponse order(@RequestBody OrderRequest request) {
        int remaining = stock.take(request.isbn(), request.quantity());
        return new OrderResponse(request.isbn(), request.quantity(), remaining);
    }
}
