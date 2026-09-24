package com.springbootedu.graphqlwebsocket.notify;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

/**
 * Lesson 3.6 — body of PUT /api/books/{isbn}/price.
 */
record PriceChangeRequest(@NotNull @Positive BigDecimal price) {
}
