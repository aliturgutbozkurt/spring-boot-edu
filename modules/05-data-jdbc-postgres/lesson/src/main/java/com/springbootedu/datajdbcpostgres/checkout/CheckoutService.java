package com.springbootedu.datajdbcpostgres.checkout;

import com.springbootedu.datajdbcpostgres.book.Book;
import com.springbootedu.datajdbcpostgres.book.JdbcBookRepository;
import com.springbootedu.datajdbcpostgres.order.OrderLine;
import com.springbootedu.datajdbcpostgres.order.PurchaseOrder;
import com.springbootedu.datajdbcpostgres.order.PurchaseOrderRepository;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Lesson 3.5 — one business operation, one transaction: all changes commit together or none do.
 */
@Service
public class CheckoutService {

    private final JdbcBookRepository books;
    private final PurchaseOrderRepository orders;
    private final AuditLog audit;
    private final ApplicationEventPublisher events;

    public CheckoutService(JdbcBookRepository books, PurchaseOrderRepository orders, AuditLog audit,
                           ApplicationEventPublisher events) {
        this.books = books;
        this.orders = orders;
        this.audit = audit;
        this.events = events;
    }

    // tag::transactional[]
    @Transactional                                          // begin … commit, or rollback on a RuntimeException
    public long placeOrder(String customerEmail, Map<String, Integer> quantities) {
        audit.record("Checkout started by " + customerEmail);     // separate transaction (REQUIRES_NEW)

        Set<OrderLine> lines = new HashSet<>();
        quantities.forEach((isbn, quantity) -> {
            Book book = books.findByIsbn(isbn).orElseThrow(() -> new IllegalArgumentException("Unknown ISBN " + isbn));
            if (book.stock() < quantity) {
                throw new OutOfStockException(isbn, quantity, book.stock());   // → everything is rolled back
            }
            books.changeStock(isbn, -quantity);
            lines.add(new OrderLine(isbn, quantity, book.price()));
        });

        long orderId = Objects.requireNonNull(orders.save(PurchaseOrder.create(customerEmail, lines)).id());
        events.publishEvent(new OrderPlacedEvent(orderId, customerEmail));    // delivered after commit
        return orderId;
    }
    // end::transactional[]
}
