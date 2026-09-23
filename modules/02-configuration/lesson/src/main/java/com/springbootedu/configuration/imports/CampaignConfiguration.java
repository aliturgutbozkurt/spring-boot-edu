package com.springbootedu.configuration.imports;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Lesson 3.5 — registers {@link CampaignProperties}.
 */
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(CampaignProperties.class)
public class CampaignConfiguration {
}
