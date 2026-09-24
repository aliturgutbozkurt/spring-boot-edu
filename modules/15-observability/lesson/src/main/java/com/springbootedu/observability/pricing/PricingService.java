package com.springbootedu.observability.pricing;

import io.micrometer.observation.annotation.Observed;
import java.math.BigDecimal;
import org.springframework.stereotype.Service;

/**
 * Lesson 3.4 — @Observed creates an observation: Micrometer turns it into a timer AND a span.
 */
// tag::observed[]
@Service
public class PricingService {

    @Observed(name = "bookstore.pricing",                               // metric name
              contextualName = "calculate-price",                       // span name
              lowCardinalityKeyValues = {"currency", "TRY"})            // tag on both
    public BigDecimal priceOf(String isbn) {
        sleep(50);                                                      // pretend to ask a pricing engine
        return isbn.endsWith("1") ? new BigDecimal("89.90") : new BigDecimal("55.00");
    }
    // end::observed[]

    private static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
