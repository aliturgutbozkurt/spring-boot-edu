package com.springbootedu.capstone.order.order;

import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.UUID;
import org.jspecify.annotations.Nullable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * The orders of the signed-in customer. The customer ID is the "sub" of the JWT — never a request parameter.
 */
@RestController
@RequestMapping("/api/orders")
class OrderController {

    private final OrderService orders;

    OrderController(OrderService orders) {
        this.orders = orders;
    }

    @PostMapping
    ResponseEntity<OrderResponse> place(@AuthenticationPrincipal Jwt customer,
                                        @RequestHeader(name = "Idempotency-Key", required = false) @Nullable String key,
                                        @Valid @RequestBody PlaceOrderRequest request) {
        OrderService.Placed placed = orders.place(customer.getSubject(), key, request);
        var response = placed.created() ? ResponseEntity.created(URI.create("/api/orders/" + placed.order().id()))
                : ResponseEntity.ok().location(URI.create("/api/orders/" + placed.order().id()));   // a retry: 200
        return response.body(placed.order());
    }

    @GetMapping
    List<OrderResponse> mine(@AuthenticationPrincipal Jwt customer) {
        return orders.ordersOf(customer.getSubject());
    }

    @GetMapping("/{id}")
    OrderResponse find(@AuthenticationPrincipal Jwt customer, @PathVariable UUID id) {
        return orders.find(customer.getSubject(), id);
    }

    @DeleteMapping("/{id}")
    ResponseEntity<Void> cancel(@AuthenticationPrincipal Jwt customer, @PathVariable UUID id) {
        orders.cancel(customer.getSubject(), id);
        return ResponseEntity.noContent().build();
    }
}
