package com.springbootedu.testing.exercise3.shop.repository;

import java.util.List;

/**
 * Given: the data layer of the shop.
 */
public class ShopRepository {

    public List<String> titles() {
        return List.of("Effective Java", "Spring in Action");
    }
}
