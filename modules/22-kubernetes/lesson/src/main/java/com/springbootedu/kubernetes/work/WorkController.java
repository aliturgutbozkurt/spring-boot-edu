package com.springbootedu.kubernetes.work;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Lesson 3.6 — burns CPU for a while, so that the HorizontalPodAutoscaler has something to react to.
 */
@RestController
class WorkController {

    @GetMapping("/api/work")
    String work(@RequestParam(defaultValue = "50") long millis) {
        long end = System.nanoTime() + millis * 1_000_000;
        long counter = 0;
        while (System.nanoTime() < end) {
            counter++;                                        // busy on purpose: this is CPU load, not waiting
        }
        return "worked " + millis + " ms (" + counter + " loops) on " + System.getenv().getOrDefault("HOSTNAME", "?");
    }
}
