package com.springbootedu.springcloud.fallback;

import org.springframework.web.bind.annotation.RestController;

/**
 * Exercise 2 — what the client gets while the review service fails: an empty list, clearly marked.
 */
@RestController
class FallbackController {

    // TODO 2b: /fallback/reviews answers {"reviews": [], "fallback": true} (for any HTTP method)
}
