package com.springbootedu.corecontainer.event;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.springframework.stereotype.Component;

/**
 * Lesson 3.7 — where the listeners write, so the effects of events are visible (and testable).
 */
@Component
public class NotificationLog {

    private final List<String> entries = new CopyOnWriteArrayList<>();

    public void add(String entry) {
        entries.add(entry);
    }

    public List<String> entries() {
        return List.copyOf(entries);
    }
}
