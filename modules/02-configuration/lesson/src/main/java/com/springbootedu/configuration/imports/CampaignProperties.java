package com.springbootedu.configuration.imports;

import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

/**
 * Lesson 3.5 — bound from campaigns.yaml, which application.yaml imports.
 *
 * @param active running campaigns
 */
@ConfigurationProperties("bookstore.campaigns")
public record CampaignProperties(@DefaultValue List<Campaign> active) {

    /**
     * @param code    campaign code customers type in
     * @param percent discount in percent
     */
    public record Campaign(String code, int percent) {
    }
}
