package com.springbootedu.nativeperformance.exercise2;

import java.time.Duration;

/**
 * Exercise 2 — how long the creation of one bean took.
 */
public record BeanTiming(String beanName, Duration duration) {
}
