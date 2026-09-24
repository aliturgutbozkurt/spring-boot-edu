package com.springbootedu.modulith.order;

import com.springbootedu.modulith.catalog.Book;
import com.springbootedu.modulith.catalog.CatalogService;
import com.springbootedu.modulith.order.internal.OrderRepository;
import java.math.BigDecimal;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Lesson 3.2 — places an order and tells the rest of the application with an event,
 * without knowing who listens (inventory, notification, Kafka).
 */
@Service
public class OrderService {

    private final CatalogService catalog;
    private final OrderRepository repository;
    private final ApplicationEventPublisher events;

    OrderService(CatalogService catalog, OrderRepository repository, ApplicationEventPublisher events) {
        this.catalog = catalog;
        this.repository = repository;
        this.events = events;
    }

    // tag::publish[]
    @Transactional
    public Order place(String customerId, String isbn, int quantity) {
        Book book = catalog.find(isbn).orElseThrow(() -> new UnknownBookException(isbn));
        BigDecimal total = book.price().multiply(BigDecimal.valueOf(quantity));
        Order order = repository.save(customerId, isbn, quantity, total);

        // stored in the registry in THIS transaction; delivered to the listeners after the commit
        events.publishEvent(new OrderPlaced(order.id(), customerId, isbn, quantity, total));
        return order;
    }
    // end::publish[]
}
