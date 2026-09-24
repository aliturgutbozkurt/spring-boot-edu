package com.springbootedu.testing.order;

import com.springbootedu.testing.audit.AuditLog;
import com.springbootedu.testing.book.Book;
import com.springbootedu.testing.book.BookNotFoundException;
import com.springbootedu.testing.book.BookRepository;
import com.springbootedu.testing.payment.PaymentClient;
import com.springbootedu.testing.payment.PaymentReceipt;
import com.springbootedu.testing.payment.PaymentRequest;
import com.springbootedu.testing.pricing.PriceCalculator;
import java.math.BigDecimal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Lessons 3.2 and 3.6 — the use case: check stock, calculate the price, charge, record.
 */
// tag::service[]
@Service
public class OrderService {

    private final BookRepository books;
    private final PriceCalculator prices;
    private final PaymentClient payments;
    private final AuditLog audit;

    public OrderService(BookRepository books, PriceCalculator prices, PaymentClient payments, AuditLog audit) {
        this.books = books;
        this.prices = prices;
        this.payments = payments;
        this.audit = audit;
    }

    @Transactional
    public OrderConfirmation placeOrder(String isbn, int quantity) {
        Book book = books.findByIsbn(isbn).orElseThrow(() -> new BookNotFoundException(isbn));
        book.removeFromStock(quantity);                                     // may throw OutOfStockException
        BigDecimal total = prices.total(book.getPrice(), quantity);
        PaymentReceipt receipt = payments.charge(new PaymentRequest("order-" + isbn, total));
        audit.record("ordered " + quantity + " × " + isbn + " for " + total);
        return new OrderConfirmation(isbn, book.getTitle(), quantity, total, receipt.id());
    }
}
// end::service[]
