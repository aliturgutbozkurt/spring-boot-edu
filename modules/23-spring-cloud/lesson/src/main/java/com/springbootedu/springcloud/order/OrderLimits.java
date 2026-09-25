package com.springbootedu.springcloud.order;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

/**
 * Lesson 3.5 — a value from the Config Server. @RefreshScope creates the bean again after a refresh.
 */
// tag::refresh-scope[]
@Component
@RefreshScope
public class OrderLimits {

    private final int maxQuantity;

    OrderLimits(@Value("${bookstore.order.max-quantity:10}") int maxQuantity) {
        this.maxQuantity = maxQuantity;
    }

    public int maxQuantity() {
        return maxQuantity;
    }
}
// end::refresh-scope[]
