package com.springbootedu.springcloud.fallback;

import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Exercise 2 — what the client gets while the review service fails: an empty list, clearly marked.
 */
@RestController
class FallbackController {

    @RequestMapping("/fallback/reviews")
    Map<String, Object> reviews() {
        return Map.of("reviews", List.of(), "fallback", true);
    }
}
