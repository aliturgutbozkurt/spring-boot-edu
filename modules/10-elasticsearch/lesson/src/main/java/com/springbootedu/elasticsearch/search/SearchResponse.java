package com.springbootedu.elasticsearch.search;

import java.util.List;
import java.util.Map;

/**
 * Lesson 3.5 — what the search API returns: the hits and a count per category.
 */
public record SearchResponse(List<BookHit> hits, Map<String, Long> categories) {
}
