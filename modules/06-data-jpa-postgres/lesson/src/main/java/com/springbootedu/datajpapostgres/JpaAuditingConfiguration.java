package com.springbootedu.datajpapostgres;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Lesson 3.6 — switches on @CreatedDate / @LastModifiedDate.
 * A separate class (not on the application class) so that @DataJpaTest slices can opt in with @Import.
 */
@Configuration(proxyBeanMethods = false)
@EnableJpaAuditing
public class JpaAuditingConfiguration {
}
