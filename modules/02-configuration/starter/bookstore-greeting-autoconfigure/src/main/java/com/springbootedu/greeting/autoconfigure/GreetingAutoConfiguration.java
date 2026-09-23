package com.springbootedu.greeting.autoconfigure;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBooleanProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * Lesson 3.6 — auto-configuration = a configuration class guarded by conditions,
 * listed in META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports.
 */
// tag::auto-configuration[]
@AutoConfiguration
@ConditionalOnClass(GreetingService.class)                       // only if the class is on the classpath
@ConditionalOnBooleanProperty(name = "bookstore.greeting.enabled", matchIfMissing = true)
@EnableConfigurationProperties(GreetingProperties.class)
public class GreetingAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean                                     // back off if the application has its own
    GreetingService greetingService(GreetingProperties properties) {
        return name -> properties.prefix() + ", " + name + properties.suffix();
    }
}
// end::auto-configuration[]
