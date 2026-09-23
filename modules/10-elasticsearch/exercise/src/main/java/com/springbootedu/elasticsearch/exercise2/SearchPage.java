package com.springbootedu.elasticsearch.exercise2;

import java.util.List;
import java.util.Map;

/**
 * Given: one page of search results with a count per category.
 */
public record SearchPage(List<ProductDocument> hits, Map<String, Long> categories) {
}
