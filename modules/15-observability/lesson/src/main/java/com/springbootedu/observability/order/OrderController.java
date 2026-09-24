package com.springbootedu.observability.order;

import com.springbootedu.observability.catalog.CatalogClient;
import com.springbootedu.observability.pricing.PricingService;
import io.micrometer.tracing.Span;
import io.micrometer.tracing.Tracer;
import java.math.BigDecimal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Lesson 3.5 — one request, several spans: HTTP server → pricing (@Observed) → HTTP client → catalog.
 */
@RestController
class OrderController {

    private static final Logger log = LoggerFactory.getLogger(OrderController.class);

    private final OrderMetrics metrics;
    private final PricingService pricing;
    private final CatalogClient catalog;
    private final Tracer tracer;

    OrderController(OrderMetrics metrics, PricingService pricing, CatalogClient catalog, Tracer tracer) {
        this.metrics = metrics;
        this.pricing = pricing;
        this.catalog = catalog;
        this.tracer = tracer;
    }

    @PostMapping("/api/orders")
    OrderResult place(@RequestParam String isbn, @RequestParam(defaultValue = "web") String channel) {
        return metrics.recordOrder(channel, () -> {
            BigDecimal price = pricing.priceOf(isbn);
            String title = catalog.titleOf(isbn);
            log.info("order placed for {} via {}", isbn, channel);           // the log line carries trace.id
            Span span = tracer.currentSpan();
            return new OrderResult(isbn, title, price, span == null ? "" : span.context().traceId());
        });
    }
}
