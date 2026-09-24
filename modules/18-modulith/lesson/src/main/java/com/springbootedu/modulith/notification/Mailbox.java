package com.springbootedu.modulith.notification;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.springframework.stereotype.Component;

/**
 * Lesson 3.3 — stands for an e-mail service: it only remembers what was "sent".
 */
@Component
public class Mailbox {

    private record Mail(String customerId, String text) {
    }

    private final List<Mail> sent = new CopyOnWriteArrayList<>();

    void send(String customerId, String text) {
        sent.add(new Mail(customerId, text));
    }

    public List<String> sentTo(String customerId) {
        return sent.stream().filter(mail -> mail.customerId().equals(customerId)).map(Mail::text).toList();
    }
}
