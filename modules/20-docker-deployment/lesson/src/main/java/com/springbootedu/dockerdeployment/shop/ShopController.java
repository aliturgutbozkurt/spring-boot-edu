package com.springbootedu.dockerdeployment.shop;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Lesson 3.5 — shows the configuration and the host name (in a container: the container ID).
 */
@RestController
@EnableConfigurationProperties(ShopProperties.class)
class ShopController {

    private final ShopProperties shop;

    ShopController(ShopProperties shop) {
        this.shop = shop;
    }

    @GetMapping("/api/shop")
    Map<String, String> shop() throws UnknownHostException {
        return Map.of("name", shop.name(), "currency", shop.currency(), "host", InetAddress.getLocalHost().getHostName());
    }
}
