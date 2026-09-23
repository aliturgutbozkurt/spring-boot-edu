package com.springbootedu.httpclientsresilience.resilience;

import com.springbootedu.httpclientsresilience.catalog.Price;
import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;

/**
 * Lesson 3.6 — uses the retrying query and falls back to the last known price when retries are exhausted.
 */
@Service
public class PriceService {

    private static final Logger log = LoggerFactory.getLogger(PriceService.class);

    private final PriceQuery query;
    private final Map<String, Price> lastKnown = new ConcurrentHashMap<>(
            Map.of("9780134685991", new Price(new BigDecimal("85.00"), "TRY")));

    public PriceService(PriceQuery query) {
        this.query = query;
    }

    public Price currentPrice(String isbn) {
        Price price = query.fetch(isbn);                    // through the proxy → retries apply
        lastKnown.put(isbn, price);
        return price;
    }

    // tag::fallback[]
    public Price priceOrLastKnown(String isbn) {
        try {
            return currentPrice(isbn);
        } catch (RestClientException exception) {
            log.warn("Catalog unavailable after retries, using the last known price: {}", exception.getMessage());
            Price fallback = lastKnown.get(isbn);
            if (fallback == null) {
                throw exception;
            }
            return fallback;
        }
    }
    // end::fallback[]
}
