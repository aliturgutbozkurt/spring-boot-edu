package com.springbootedu.datamongodb.catalog;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Test data shared by the catalog tests: MongoDB has no migrations, so every test inserts what it needs.
 */
final class CatalogFixture {

    private CatalogFixture() {
    }

    static List<Book> books(Publisher addison, Publisher manning) {
        return List.of(
                new Book(null, "9780134685991", "Effective Java", List.of("Joshua Bloch"), new BigDecimal("89.90"), 5,
                        List.of("java", "best-practices"), Map.of("language", "en", "pages", "412"), addison,
                        List.of(new Review("ayse", 5, "Harika"), new Review("mehmet", 4, "Çok iyi"))),
                new Book(null, "9780321336781", "Java Puzzlers", List.of("Joshua Bloch", "Neal Gafter"),
                        new BigDecimal("55.00"), 1, List.of("java"), Map.of("language", "en"), addison,
                        List.of(new Review("zeynep", 3, "Eğlenceli"))),
                new Book(null, "9781617297571", "Spring in Action", List.of("Craig Walls"), new BigDecimal("95.00"), 4,
                        List.of("spring", "java"), Map.of("language", "en", "edition", "6"), manning, List.of()),
                new Book(null, "9786050000011", "Kürk Mantolu Madonna", List.of("Sabahattin Ali"), new BigDecimal("45.00"),
                        10, List.of("roman"), Map.of("language", "tr"), null, List.of(new Review("ayse", 5, "Klasik"))));
    }
}
