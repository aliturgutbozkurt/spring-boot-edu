package com.springbootedu.httpclientsresilience.resilience;

import com.springbootedu.httpclientsresilience.catalog.CatalogApi;
import com.springbootedu.httpclientsresilience.catalog.Price;
import org.springframework.resilience.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpServerErrorException;

/**
 * Lesson 3.6 — the remote call with retries. A separate bean, so callers go through the retry proxy.
 */
// tag::retryable[]
@Component
public class PriceQuery {

    private final CatalogApi catalog;

    public PriceQuery(CatalogApi catalog) {
        this.catalog = catalog;
    }

    @Retryable(
            includes = HttpServerErrorException.class,    // only 5xx: a 404 will not get better by retrying
            maxRetries = 3,                                // 1 call + up to 3 retries
            delay = 100, multiplier = 2, jitter = 20)      // wait ~100 ms, ~200 ms, ~400 ms
    public Price fetch(String isbn) {
        return catalog.price(isbn);
    }
}
// end::retryable[]
