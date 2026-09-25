package com.springbootedu.dockerdeployment.warehouse;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

/**
 * Given — where the warehouse service runs (BOOKSTORE_WAREHOUSE_HOST / BOOKSTORE_WAREHOUSE_PORT in a container).
 */
@ConfigurationProperties("bookstore.warehouse")
public record WarehouseProperties(@DefaultValue("localhost") String host, @DefaultValue("9000") int port) {
}
