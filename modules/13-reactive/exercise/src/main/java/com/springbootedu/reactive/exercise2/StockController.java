package com.springbootedu.reactive.exercise2;

import org.springframework.web.bind.annotation.RestController;

/**
 * Exercise 2 — update stock levels and follow them live.
 */
@RestController
public class StockController {

    private final StockFeed feed;

    public StockController(StockFeed feed) {
        this.feed = feed;
    }

    // TODO 2a: PUT /api/stock/{isbn} with the new quantity as body → publish a StockLevel, answer 204
    // TODO 2b: GET /api/stock/stream as text/event-stream: every change as a Server-Sent Event
    // TODO 2c: with ?isbn=… only the changes of that book
    // TODO 2d: send a comment event first, so that the client gets the response headers at once
}
