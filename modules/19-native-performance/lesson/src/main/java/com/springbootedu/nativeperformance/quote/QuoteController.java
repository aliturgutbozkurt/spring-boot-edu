package com.springbootedu.nativeperformance.quote;

import java.time.Clock;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Lesson 3.4 — GET /api/quote.
 */
@RestController
class QuoteController {

    private final QuoteOfTheDay quotes = new QuoteOfTheDay(Clock.systemDefaultZone());

    @GetMapping("/api/quote")
    String quote() {
        return quotes.today();
    }
}
