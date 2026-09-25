package com.springbootedu.capstone.search;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Capstone — the search service: a read model in Elasticsearch, fed by events, cached in Redis (ADR-4, ADR-7).
 */
@SpringBootApplication
public class SearchServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(SearchServiceApplication.class, args);
    }
}
