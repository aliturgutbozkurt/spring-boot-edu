package com.springbootedu.capstone.search.query;

import java.util.List;

/**
 * The answer of GET /api/search — also the value stored in the Redis cache.
 */
public record SearchResult(String query, List<BookHit> hits) {
}
