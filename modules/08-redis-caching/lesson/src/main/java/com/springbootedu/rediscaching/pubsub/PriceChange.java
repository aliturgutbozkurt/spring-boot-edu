package com.springbootedu.rediscaching.pubsub;

import java.math.BigDecimal;

/**
 * Lesson 3.6 — the message sent over the "price-changes" channel.
 */
public record PriceChange(String isbn, BigDecimal newPrice) {
}
