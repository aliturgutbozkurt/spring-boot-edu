package com.springbootedu.nativeperformance.exercise2;

import java.util.Comparator;
import java.util.List;
import org.springframework.boot.context.metrics.buffering.BufferingApplicationStartup;
import org.springframework.boot.context.metrics.buffering.StartupTimeline.TimelineEvent;
import org.springframework.core.metrics.StartupStep;

/**
 * Exercise 2 — which beans make the startup slow?
 */
public final class StartupReport {

    private static final String BEAN_INSTANTIATION = "spring.beans.instantiate";

    private StartupReport() {
    }

    public static List<BeanTiming> slowestBeans(BufferingApplicationStartup startup, int limit) {
        return startup.getBufferedTimeline().getEvents().stream()
                .filter(event -> event.getStartupStep().getName().equals(BEAN_INSTANTIATION))
                .map(StartupReport::toTiming)
                .sorted(Comparator.comparing(BeanTiming::duration).reversed())
                .limit(limit)
                .toList();
    }

    private static BeanTiming toTiming(TimelineEvent event) {
        String beanName = "?";
        for (StartupStep.Tag tag : event.getStartupStep().getTags()) {
            if (tag.getKey().equals("beanName")) {
                beanName = tag.getValue();
            }
        }
        return new BeanTiming(beanName, event.getDuration());
    }
}
