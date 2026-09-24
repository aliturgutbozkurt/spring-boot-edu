package com.springbootedu.testing.exercise3.shop.web;

import com.springbootedu.testing.exercise3.shop.service.ShopService;
import java.util.List;

/**
 * Given: the web layer — it talks only to the service layer.
 */
public class ShopController {

    private final ShopService service;

    public ShopController(ShopService service) {
        this.service = service;
    }

    public List<String> catalog() {
        return service.catalog();
    }
}
