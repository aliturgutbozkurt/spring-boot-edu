package com.springbootedu.reactive.exercise2;

import org.jspecify.annotations.Nullable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

/**
 * Exercise 2 — update stock levels and follow them live.
 */
@RestController
@RequestMapping("/api/stock")
public class StockController {

    private final StockFeed feed;

    public StockController(StockFeed feed) {
        this.feed = feed;
    }

    @PutMapping("/{isbn}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void update(@PathVariable String isbn, @RequestBody int quantity) {
        feed.publish(new StockLevel(isbn, quantity));
    }

    @GetMapping(path = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<StockLevel>> stream(@RequestParam(required = false) @Nullable String isbn) {
        return feed.changes()
                .filter(level -> isbn == null || level.isbn().equals(isbn))
                .map(level -> ServerSentEvent.builder(level).event("stock").build())
                .startWith(ServerSentEvent.<StockLevel>builder().comment("connected").build());
    }
}
