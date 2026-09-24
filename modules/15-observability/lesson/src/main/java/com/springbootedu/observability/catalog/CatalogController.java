package com.springbootedu.observability.catalog;

import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/**
 * Lesson 3.5 — plays the "catalog service". In a real system it would run in another application.
 */
@RestController
class CatalogController {

    private static final Logger log = LoggerFactory.getLogger(CatalogController.class);

    @GetMapping("/api/catalog/{isbn}")
    Map<String, String> book(@PathVariable String isbn) {
        log.info("catalog lookup for {}", isbn);                        // same trace id as the caller
        return Map.of("isbn", isbn, "title", isbn.endsWith("1") ? "Effective Java" : "Java Puzzlers");
    }
}
