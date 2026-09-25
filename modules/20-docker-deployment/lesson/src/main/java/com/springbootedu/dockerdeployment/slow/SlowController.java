package com.springbootedu.dockerdeployment.slow;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Lesson 3.4 — a slow request, to see graceful shutdown: "docker stop" while it runs, and it still finishes.
 */
@RestController
class SlowController {

    @GetMapping("/api/slow")
    String slow(@RequestParam(defaultValue = "3000") long millis) throws InterruptedException {
        Thread.sleep(millis);
        return "done after " + millis + " ms";
    }
}
