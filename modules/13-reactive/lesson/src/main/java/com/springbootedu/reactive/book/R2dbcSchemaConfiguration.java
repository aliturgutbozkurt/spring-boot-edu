package com.springbootedu.reactive.book;

import io.r2dbc.spi.Option;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.r2dbc.autoconfigure.ConnectionFactoryOptionsBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Lesson 3.4 — R2DBC connections work in the same schema that Flyway migrated. A customizer is used because
 * it also applies when the connection comes from Docker Compose or Testcontainers.
 */
@Configuration(proxyBeanMethods = false)
class R2dbcSchemaConfiguration {

    @Bean
    ConnectionFactoryOptionsBuilderCustomizer searchPath(@Value("${spring.flyway.schemas}") String schema) {
        return options -> options.option(Option.valueOf("schema"), schema);          // the PostgreSQL driver's option
    }
}
