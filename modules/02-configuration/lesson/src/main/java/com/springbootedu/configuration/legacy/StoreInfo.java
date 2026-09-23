package com.springbootedu.configuration.legacy;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Lesson 3.3 — {@code @Value} injects single values. Compare with {@code StoreProperties}.
 */
// tag::value[]
@Component
public class StoreInfo {

    private final String name;
    private final String phone;
    private final int maxBooksPerOrder;

    public StoreInfo(@Value("${bookstore.store.name}") String name,                       // required
                     @Value("${bookstore.store.phone:+90 212 000 00 00}") String phone,   // with default
                     @Value("#{2 + 3}") int maxBooksPerOrder) {                           // SpEL expression
        this.name = name;
        this.phone = phone;
        this.maxBooksPerOrder = maxBooksPerOrder;
    }
    // end::value[]

    public String describe() {
        return name + " · " + phone + " · max " + maxBooksPerOrder + " kitap / books";
    }
}
