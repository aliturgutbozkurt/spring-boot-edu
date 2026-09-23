package com.springbootedu.corecontainer.registrar;

/**
 * Lesson 3.6 — implementations are plain classes; {@link NotificationChannelsRegistrar} decides which become beans.
 */
public interface NotificationChannel {

    String name();

    default String send(String message) {
        return "[" + name() + "] " + message;
    }
}
