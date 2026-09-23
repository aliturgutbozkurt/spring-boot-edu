package com.springbootedu.elasticsearch.search;

import org.jspecify.annotations.Nullable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Lesson 3.5 — GET /api/search?q=java&amp;category=programming&amp;maxPrice=60
 */
// tag::controller[]
@RestController
@RequestMapping("/api/search")
class SearchController {

    private final BookSearch search;

    SearchController(BookSearch search) {
        this.search = search;
    }

    @GetMapping
    SearchResponse search(@RequestParam String q,
                          @RequestParam(required = false) @Nullable String category,
                          @RequestParam(required = false) @Nullable Double maxPrice) {
        return new SearchResponse(search.search(q, category, maxPrice), search.categoryFacets(q));
    }
}
// end::controller[]
