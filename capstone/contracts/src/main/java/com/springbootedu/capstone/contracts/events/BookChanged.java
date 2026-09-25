package com.springbootedu.capstone.contracts.events;

import java.math.BigDecimal;
import java.util.List;

/**
 * Published by the catalog service (topic bookstore.catalog, key = ISBN) when a book is created or changed.
 */
public record BookChanged(String isbn, String title, List<String> authors, String description, BigDecimal price,
                          int stock) {

    public static final String TOPIC = "bookstore.catalog";
}
