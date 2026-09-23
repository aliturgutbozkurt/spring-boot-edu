package com.springbootedu.corecontainer.exercise3;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Component;

/**
 * Exercise 3 — given: the authors our customer follows.
 */
@Component
public class FollowedAuthors {

    private final Set<String> authors = ConcurrentHashMap.newKeySet();

    public void follow(String author) {
        authors.add(author);
    }

    public boolean isFollowed(String author) {
        return authors.contains(author);
    }
}
