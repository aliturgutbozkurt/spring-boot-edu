package com.springbootedu.greeting.autoconfigure;

/**
 * Lesson 3.6 — what the starter offers. Applications can provide their own implementation.
 */
@FunctionalInterface
public interface GreetingService {

    String greet(String name);
}
