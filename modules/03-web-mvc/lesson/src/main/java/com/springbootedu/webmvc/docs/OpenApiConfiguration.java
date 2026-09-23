package com.springbootedu.webmvc.docs;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Lesson 3.8 — metadata for the generated OpenAPI document (/v3/api-docs, UI at /swagger-ui.html).
 */
// tag::openapi[]
@Configuration(proxyBeanMethods = false)
public class OpenApiConfiguration {

    @Bean
    OpenAPI bookstoreOpenApi() {
        return new OpenAPI().info(new Info()
                .title("Kitapçı API / Bookstore API")
                .version("v1, v2 (header: API-Version)")
                .description("Spring Boot Edu — module 03"));
    }
}
// end::openapi[]
