package com.springbootedu.setupmodernjava.scopedvalues;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Lesson 3.6 — collects audit entries.
 */
public class AuditLog {

    private final List<String> entries = new CopyOnWriteArrayList<>();

    public void add(String entry) {
        entries.add(entry);
    }

    public List<String> entries() {
        return List.copyOf(entries);
    }
}
