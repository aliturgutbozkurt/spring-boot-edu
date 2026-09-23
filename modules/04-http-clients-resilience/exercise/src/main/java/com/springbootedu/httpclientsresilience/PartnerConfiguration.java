package com.springbootedu.httpclientsresilience;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

/**
 * Given: a RestClient for the partner services, used by exercises 2 and 3.
 */
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(PartnerProperties.class)
public class PartnerConfiguration {

    @Bean
    RestClient partnerRestClient(RestClient.Builder builder, PartnerProperties properties) {
        return builder.baseUrl(properties.baseUrl()).build();
    }
}
