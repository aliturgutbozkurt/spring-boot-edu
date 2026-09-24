package com.springbootedu.graphqlwebsocket.exercise2;

import com.springbootedu.graphqlwebsocket.catalog.Catalog;
import org.springframework.stereotype.Controller;

/**
 * Exercise 2 — Mutation.placeOrder, Order.total, OrderLine.book and the errors.
 */
@Controller
class OrderController {

    private final OrderService orders;
    private final Catalog catalog;

    OrderController(OrderService orders, Catalog catalog) {
        this.orders = orders;
        this.catalog = catalog;
    }

    // TODO 2a: Mutation.placeOrder — let OrderService place the order

    // TODO 2b: Order.total — the sum of unit price × quantity of all lines (PlacedOrder has no total field)

    // TODO 2c: OrderLine.book — the catalog book of the line's ISBN

    // TODO 2d: errors — InvalidOrderException and OutOfStockException → BAD_REQUEST, UnknownBookException → NOT_FOUND
}
