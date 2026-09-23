package com.springbootedu.elasticsearch.exercise3;

import java.util.List;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Given: an empty catalog so that the application starts; tests provide their own {@link BookSource}.
 */
@Configuration(proxyBeanMethods = false)
class NoBooks {

    @Bean
    @ConditionalOnMissingBean
    BookSource bookSource() {
        return List::of;
    }
}
