package com.springbootedu.security.exercise2;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;
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

    @PreAuthorize("#customer == authentication.name or hasRole('ADMIN')")
    public List<CustomerOrder> ordersOf(String customer) {
        return ORDERS.stream().filter(order -> order.customer().equals(customer)).toList();
    }

    @PostFilter("filterObject.customer() == authentication.name")
    public List<CustomerOrder> recentOrders() {
        return new ArrayList<>(ORDERS);                             // @PostFilter needs a mutable collection
    }

    @PostAuthorize("returnObject.customer() == authentication.name or hasRole('ADMIN')")
    public CustomerOrder find(long id) {
        return ORDERS.stream().filter(order -> order.id() == id).findFirst()
                .orElseThrow(() -> new NoSuchElementException("No order " + id));
    }
}
