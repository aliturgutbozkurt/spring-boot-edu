package com.springbootedu.corecontainer.exercise3;

import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
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

    @EventListener(condition = "@followedAuthors.isFollowed(#event.author())")
    @Order(1)
    public void onBookAdded(BookAddedEvent event) {
        inbox.add("Takip ettiğiniz yazar / Followed author: " + event.author() + " — " + event.title());
    }
}
