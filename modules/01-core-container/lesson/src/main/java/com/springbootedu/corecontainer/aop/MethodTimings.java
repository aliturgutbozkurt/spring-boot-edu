package com.springbootedu.corecontainer.aop;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.springframework.stereotype.Component;

/**
 * Lesson 3.8 — collects which methods the aspect measured (used by tests and the demo).
 */
@Component
public class MethodTimings {

    private final List<String> recorded = new CopyOnWriteArrayList<>();

    void record(String method) {
        recorded.add(method);
    }

    public List<String> recorded() {
        return List.copyOf(recorded);
    }
}
