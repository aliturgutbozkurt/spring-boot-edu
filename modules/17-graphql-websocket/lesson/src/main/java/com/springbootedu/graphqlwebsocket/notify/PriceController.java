package com.springbootedu.graphqlwebsocket.notify;

import jakarta.validation.Valid;
import java.math.BigDecimal;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

/**
 * Lesson 3.6 — a normal REST endpoint that also pushes the change to every connected WebSocket client.
 */
@RestController
class PriceController {

    private final JdbcClient jdbc;
    private final SimpMessagingTemplate messaging;

    PriceController(JdbcClient jdbc, SimpMessagingTemplate messaging) {
        this.jdbc = jdbc;
        this.messaging = messaging;
    }

    // tag::push[]
    @PutMapping("/api/books/{isbn}/price")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void changePrice(@PathVariable String isbn, @Valid @RequestBody PriceChangeRequest request) {
        BigDecimal price = request.price();
        int updated = jdbc.sql("UPDATE book SET price = ? WHERE isbn = ?").params(price, isbn).update();
        if (updated == 0) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No book with ISBN " + isbn);  // → ProblemDetail
        }
        messaging.convertAndSend("/topic/prices", new PriceChanged(isbn, price));   // JSON to all subscribers
    }
    // end::push[]
}
