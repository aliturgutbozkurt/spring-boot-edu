package com.springbootedu.corecontainer.exercise3;

import org.springframework.stereotype.Component;

/**
 * Exercise 3 — notifies followers of the book's author, before the audit entry is written.
 */
@Component
public class FollowerNotifier {

    private final FollowedAuthors followedAuthors;
    private final Inbox inbox;

    public FollowerNotifier(FollowedAuthors followedAuthors, Inbox inbox) {
        this.followedAuthors = followedAuthors;
        this.inbox = inbox;
    }

    // TODO 3c: make this method an event listener for BookAddedEvent
    // TODO 3d: only react when followedAuthors.isFollowed(author) is true
    // TODO 3e: run BEFORE AuditTrail (which uses @Order(10))
    public void onBookAdded(BookAddedEvent event) {
        // TODO 3f: add "Takip ettiğiniz yazar / Followed author: <author> — <title>" to the inbox
    }
}
