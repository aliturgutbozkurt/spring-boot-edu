package com.springbootedu.httpclientsresilience.fake;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBooleanProperty;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

/**
 * Plays the remote catalog service inside the lesson application, so "spring-boot:run" works without
 * any other server. The price endpoint fails twice per ISBN before it answers — to show retries.
 */
@RestController
@RequestMapping("/catalog/books")
@ConditionalOnBooleanProperty(name = "bookstore.catalog.fake-server.enabled", matchIfMissing = true)
public class FakeCatalogController {

    private static final Map<String, Map<String, Object>> BOOKS = Map.of(
            "9780134685991", Map.of("isbn", "9780134685991", "title", "Effective Java",
                    "authors", List.of("Joshua Bloch"), "pageCount", 412),
            "9781617297571", Map.of("isbn", "9781617297571", "title", "Spring in Action",
                    "authors", List.of("Craig Walls"), "pageCount", 520));

    private final Map<String, AtomicInteger> priceCalls = new ConcurrentHashMap<>();

    @GetMapping("/{isbn}")
    Map<String, Object> book(@PathVariable String isbn) {
        Map<String, Object> book = BOOKS.get(isbn);
        if (book == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return book;
    }

    @GetMapping("/{isbn}/price")
    Map<String, Object> price(@PathVariable String isbn) {
        if (priceCalls.computeIfAbsent(isbn, key -> new AtomicInteger()).incrementAndGet() <= 2) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE);   // flaky on purpose
        }
        return Map.of("amount", new BigDecimal("89.90"), "currency", "TRY");
    }

    @GetMapping("/{isbn}/cover")
    byte[] cover(@PathVariable String isbn) throws InterruptedException {
        Thread.sleep(200);                                  // a slow download
        return new byte[] {(byte) 0x89, 'P', 'N', 'G'};
    }
}
