package com.springbootedu.modulith.order.internal;

import com.springbootedu.modulith.order.Order;
import java.math.BigDecimal;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

/**
 * Lesson 3.2 — internal to the order module.
 */
@Repository
public class OrderRepository {

    private final JdbcClient jdbc;

    OrderRepository(JdbcClient jdbc) {
        this.jdbc = jdbc;
    }

    public Order save(String customerId, String isbn, int quantity, BigDecimal total) {
        long id = jdbc.sql("""
                        INSERT INTO orders (customer_id, isbn, quantity, total) VALUES (?, ?, ?, ?)
                        RETURNING id""")
                .params(customerId, isbn, quantity, total)
                .query(Long.class)
                .single();
        return new Order(id, customerId, isbn, quantity, total);
    }
}
