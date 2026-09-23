package com.springbootedu.hazelcast.member;

import com.hazelcast.config.Config;
import com.hazelcast.config.MapConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * Lesson 3.1 — a {@link Config} bean is all Boot needs to start an embedded member inside the application.
 */
// tag::member-config[]
@Configuration(proxyBeanMethods = false)
@Profile("!client")
public class MemberConfiguration {

    @Bean
    Config hazelcastConfig() {
        Config config = new Config();
        config.setClusterName("bookstore");
        config.setProperty("hazelcast.phone.home.enabled", "false");
        var join = config.getNetworkConfig().getJoin();
        join.getAutoDetectionConfig().setEnabled(false);           // a single member: do not look for others
        join.getMulticastConfig().setEnabled(false);
        config.addMapConfig(new MapConfig("prices").setTimeToLiveSeconds(600));   // cached prices expire
        return config;
    }
}
// end::member-config[]
