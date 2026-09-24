package com.springbootedu.nativeperformance.exercise2;

import java.util.List;
import org.springframework.boot.context.metrics.buffering.BufferingApplicationStartup;

/**
 * Exercise 2 — which beans make the startup slow?
 */
public final class StartupReport {

    private StartupReport() {
    }

    public static List<BeanTiming> slowestBeans(BufferingApplicationStartup startup, int limit) {
        // TODO 2a: take the recorded steps of the startup that instantiate a bean ("spring.beans.instantiate")
        // TODO 2b: turn each into a BeanTiming (the bean name is the step's tag "beanName")
        // TODO 2c: the slowest first, at most "limit" of them
        throw new UnsupportedOperationException("TODO 2 — " + startup + limit);
    }
}
