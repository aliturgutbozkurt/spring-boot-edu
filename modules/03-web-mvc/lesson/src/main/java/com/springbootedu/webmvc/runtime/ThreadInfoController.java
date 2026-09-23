package com.springbootedu.webmvc.runtime;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Lesson 3.9 — shows which kind of thread handles the request.
 */
// tag::thread-info[]
@RestController
public class ThreadInfoController {

    public record ThreadInfo(String name, boolean virtual) {
    }

    @GetMapping("/api/runtime/thread")
    public ThreadInfo current() {
        Thread thread = Thread.currentThread();
        return new ThreadInfo(thread.toString(), thread.isVirtual());
    }
}
// end::thread-info[]
