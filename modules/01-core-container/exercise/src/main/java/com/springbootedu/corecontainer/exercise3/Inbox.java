package com.springbootedu.corecontainer.exercise3;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.springframework.stereotype.Component;

/**
 * Exercise 3 — given: where listeners write their messages.
 */
@Component
public class Inbox {

    private final List<String> messages = new CopyOnWriteArrayList<>();

    public void add(String message) {
        messages.add(message);
    }

    public List<String> messages() {
        return List.copyOf(messages);
    }
}
