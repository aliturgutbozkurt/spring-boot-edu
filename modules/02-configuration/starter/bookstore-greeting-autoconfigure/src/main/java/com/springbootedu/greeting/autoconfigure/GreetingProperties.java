package com.springbootedu.greeting.autoconfigure;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

/**
 * Lesson 3.6 — the starter's settings under the "bookstore.greeting" prefix.
 *
 * @param enabled whether the starter creates a {@link GreetingService}
 * @param prefix  text before the name
 * @param suffix  text after the name
 */
// tag::properties[]
@ConfigurationProperties("bookstore.greeting")
public record GreetingProperties(
        @DefaultValue("true") boolean enabled,
        @DefaultValue("Merhaba") String prefix,
        @DefaultValue("!") String suffix) {
}
// end::properties[]
