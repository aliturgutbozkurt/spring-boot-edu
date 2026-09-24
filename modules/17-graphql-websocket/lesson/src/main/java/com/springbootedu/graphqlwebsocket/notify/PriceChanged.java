package com.springbootedu.graphqlwebsocket.notify;

import java.math.BigDecimal;

/**
 * Lesson 3.6 — the message every subscriber of /topic/prices receives (as JSON).
 */
public record PriceChanged(String isbn, BigDecimal newPrice) {
}
