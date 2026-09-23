package com.springbootedu.corecontainer.registrar;

import java.util.List;

/**
 * Lesson 3.6 — receives every registered channel: injecting a {@code List<T>} collects all beans of type T.
 */
public class NotificationService {

    private final List<NotificationChannel> channels;

    public NotificationService(List<NotificationChannel> channels) {
        this.channels = channels;
    }

    public List<String> broadcast(String message) {
        return channels.stream().map(channel -> channel.send(message)).toList();
    }
}
