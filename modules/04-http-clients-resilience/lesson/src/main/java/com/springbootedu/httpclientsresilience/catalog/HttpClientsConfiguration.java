package com.springbootedu.httpclientsresilience.catalog;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.service.registry.ImportHttpServices;

/**
 * Lesson 3.4 — registers the HTTP interface as a bean in the group "catalog".
 * Boot configures the group from spring.http.serviceclient.catalog.* (base URL, timeouts).
 */
// tag::import-http-services[]
@Configuration(proxyBeanMethods = false)
@ImportHttpServices(group = "catalog", types = CatalogApi.class)
@EnableConfigurationProperties(CatalogProperties.class)
public class HttpClientsConfiguration {
}
// end::import-http-services[]
