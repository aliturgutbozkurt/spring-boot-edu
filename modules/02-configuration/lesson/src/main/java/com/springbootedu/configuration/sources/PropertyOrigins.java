package com.springbootedu.configuration.sources;

import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertySource;
import org.springframework.stereotype.Component;

/**
 * Lesson 3.1 — the Environment is an ordered list of property sources; the first one that has the key wins.
 */
// tag::origins[]
@Component
public class PropertyOrigins {

    private final ConfigurableEnvironment environment;

    public PropertyOrigins(ConfigurableEnvironment environment) {
        this.environment = environment;
    }

    public String sourceOf(String key) {
        for (PropertySource<?> source : environment.getPropertySources()) {   // highest priority first
            if (source.getName().equals("configurationProperties")) {
                continue;                     // Boot's combined view over all other sources — skip it
            }
            if (source.containsProperty(key)) {
                return source.getName();
            }
        }
        return "(not set)";
    }
}
// end::origins[]
