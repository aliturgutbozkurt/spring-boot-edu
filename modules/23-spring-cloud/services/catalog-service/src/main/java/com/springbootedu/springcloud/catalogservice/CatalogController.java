package com.springbootedu.springcloud.catalogservice;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

/**
 * Lesson 3.2 — the catalog API that the order service and the gateway call.
 */
@RestController
class CatalogController {

    private static final Map<String, Book> BOOKS = Map.of(
            "9780134685991", new Book("9780134685991", "Effective Java", new BigDecimal("89.90"), ""),
            "9781617297571", new Book("9781617297571", "Spring in Action", new BigDecimal("95.00"), ""),
            "9781449373320", new Book("9781449373320", "Designing Data-Intensive Applications", new BigDecimal("110.00"), ""));

    private final Outage outage;
    private final String instance;

    CatalogController(Outage outage, @Value("${bookstore.instance-name:${HOSTNAME:catalog}}") String instance) {
        this.outage = outage;
        this.instance = instance;
    }

    @GetMapping("/api/books/{isbn}")
    Book find(@PathVariable String isbn) throws InterruptedException {
        Thread.sleep(outage.latency());
        if (outage.failing()) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, instance + " is failing");
        }
        Book book = BOOKS.get(isbn);
        if (book == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No book with ISBN " + isbn);
        }
        return new Book(book.isbn(), book.title(), book.price(), instance);
    }

    /** Only for the lesson: POST /admin/outage?failing=true&latencyMillis=0 */
    @PostMapping("/admin/outage")
    String outage(@RequestParam boolean failing, @RequestParam(defaultValue = "0") long latencyMillis) {
        outage.set(failing, Duration.ofMillis(latencyMillis));
        return instance + ": failing=" + failing + ", latency=" + latencyMillis + " ms";
    }
}
