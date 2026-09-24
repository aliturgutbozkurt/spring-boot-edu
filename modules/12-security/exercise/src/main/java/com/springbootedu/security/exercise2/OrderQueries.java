package com.springbootedu.security.exercise2;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import org.springframework.stereotype.Service;

/**
 * Exercise 2 — customers see only their own orders; admins see all of them.
 */
@Service
public class OrderQueries {

    private static final List<CustomerOrder> ORDERS = List.of(
            new CustomerOrder(1, "ada", "9780134685991"),
            new CustomerOrder(2, "bob", "9781617297571"),
            new CustomerOrder(3, "bob", "9780321336781"),
            new CustomerOrder(4, "ada", "9781617297571"));

    // TODO 2a: only the customer themselves or an ADMIN may call this
    public List<CustomerOrder> ordersOf(String customer) {
        return ORDERS.stream().filter(order -> order.customer().equals(customer)).toList();
    }

    // TODO 2b: remove every order that does not belong to the caller from the returned list
    public List<CustomerOrder> recentOrders() {
        return new ArrayList<>(ORDERS);                             // @PostFilter needs a mutable collection
    }

    // TODO 2c: the returned order may only be seen by its customer or an ADMIN
    public CustomerOrder find(long id) {
        return ORDERS.stream().filter(order -> order.id() == id).findFirst()
                .orElseThrow(() -> new NoSuchElementException("No order " + id));
    }
}
