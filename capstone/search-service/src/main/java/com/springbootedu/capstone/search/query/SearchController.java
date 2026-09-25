package com.springbootedu.capstone.search.query;

import jakarta.validation.constraints.NotBlank;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * GET /api/search?q=… — public: searching needs no sign-in (the gateway routes it without a token check).
 */
@RestController
@RequestMapping("/api/search")
class SearchController {

    private final BookSearch books;

    SearchController(BookSearch books) {
        this.books = books;
    }

    @GetMapping
    SearchResult search(@RequestParam @NotBlank String q) {    // blank → 400 ProblemDetail (method validation)
        return books.search(q);
    }
}
