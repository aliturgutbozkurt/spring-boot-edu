package com.springbootedu.corecontainer.registrar;

/**
 * Lesson 3.6 — no {@code @Component}: registered only when configured.
 */
public class SmsChannel implements NotificationChannel {

    @Override
    public String name() {
        return "sms";
    }
}
