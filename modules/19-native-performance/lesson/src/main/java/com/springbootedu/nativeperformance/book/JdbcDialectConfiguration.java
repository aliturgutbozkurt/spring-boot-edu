package com.springbootedu.nativeperformance.book;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jdbc.core.dialect.JdbcPostgresDialect;

/**
 * Lesson 3.2 — Boot normally asks the database for its dialect. AOT processing runs at build time, without a
 * database, and the AOT repositories need the dialect to generate SQL: so we name it. (A startup query less, too.)
 */
// tag::jdbc-dialect[]
@Configuration(proxyBeanMethods = false)
class JdbcDialectConfiguration {

    @Bean
    JdbcPostgresDialect jdbcDialect() {
        return JdbcPostgresDialect.INSTANCE;
    }
}
// end::jdbc-dialect[]
