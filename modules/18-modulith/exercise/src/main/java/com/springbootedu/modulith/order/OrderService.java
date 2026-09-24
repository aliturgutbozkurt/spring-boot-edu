package com.springbootedu.modulith.order;

import com.springbootedu.modulith.catalog.Book;
import com.springbootedu.modulith.catalog.CatalogService;
import com.springbootedu.modulith.inventory.Inventory;
import com.springbootedu.modulith.order.internal.OrderRepository;
import java.math.BigDecimal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Exercise 3 — places an order.
 */
@Service
public class OrderService {

    private final CatalogService catalog;
    private final OrderRepository repository;
    private final Inventory inventory;

    OrderService(CatalogService catalog, OrderRepository repository, Inventory inventory) {
        this.catalog = catalog;
        this.repository = repository;
        this.inventory = inventory;
    }

    @Transactional
    public Order place(String customerId, String isbn, int quantity) {
        Book book = catalog.find(isbn).orElseThrow(() -> new UnknownBookException(isbn));
        BigDecimal total = book.price().multiply(BigDecimal.valueOf(quantity));
        Order order = repository.save(customerId, isbn, quantity, total);
        // TODO 3a: the order module must not call the inventory module — publish an OrderPlaced event instead
        inventory.reserve(isbn, quantity);
        return order;
    }
}
