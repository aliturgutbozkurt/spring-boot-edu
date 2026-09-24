package com.springbootedu.security.order;

import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Lesson 3.4 — only "authenticated" on the URL; the details are decided by OrderService.
 */
@RestController
@RequestMapping("/api/orders")
class OrderController {

    private final OrderService orders;

    OrderController(OrderService orders) {
        this.orders = orders;
    }

    @GetMapping
    List<Order> all() {
        return orders.findAll();
    }

    @GetMapping("/{id}")
    Order one(@PathVariable long id) {
        return orders.find(id);
    }
}
