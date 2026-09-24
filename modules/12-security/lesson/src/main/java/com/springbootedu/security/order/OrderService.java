package com.springbootedu.security.order;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

/**
 * Lesson 3.4 — the rules sit on the service, so they hold for every caller: controller, job or test.
 */
// tag::method-security[]
@Service
public class OrderService {

    private final Map<Long, Order> orders = Map.of(
            1L, new Order(1, "ada", "9780134685991", 1),
            2L, new Order(2, "bob", "9781617297571", 2));

    @PreAuthorize("hasRole('ADMIN')")                                    // checked before the method runs
    public List<Order> findAll() {
        return List.copyOf(orders.values());
    }

    @PostAuthorize("returnObject.customer() == authentication.name or hasRole('ADMIN')")   // checked on the result
    public Order find(long id) {
        Order order = orders.get(id);
        if (order == null) {
            throw new NoSuchElementException("No order " + id);
        }
        return order;
    }
}
// end::method-security[]
