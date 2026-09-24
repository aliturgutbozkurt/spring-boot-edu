package com.springbootedu.reactive.stream;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

/**
 * Lesson 3.6 — a live order feed: every POST is pushed to all clients listening on the stream.
 */
// tag::sse[]
@RestController
@RequestMapping("/api/orders")
class OrderStreamController {

    private final Sinks.Many<OrderEvent> orders = Sinks.many().multicast().directBestEffort();   // hot: no replay

    @PostMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    void place(@RequestBody OrderEvent order) {
        orders.tryEmitNext(order);
    }

    @GetMapping(path = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    Flux<ServerSentEvent<OrderEvent>> stream() {
        var connected = ServerSentEvent.<OrderEvent>builder().comment("connected").build();   // sends the headers now
        return orders.asFlux()
                .map(order -> ServerSentEvent.builder(order).event("order").build())
                .startWith(connected);                              // the connection stays open
    }
}
// end::sse[]
