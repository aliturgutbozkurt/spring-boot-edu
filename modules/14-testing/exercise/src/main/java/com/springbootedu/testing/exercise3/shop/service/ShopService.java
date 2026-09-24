package com.springbootedu.testing.exercise3.shop.service;

import com.springbootedu.testing.exercise3.shop.repository.ShopRepository;
import java.util.List;

/**
 * Given: the service layer — the only layer that uses the repository.
 */
public class ShopService {

    private final ShopRepository repository;

    public ShopService(ShopRepository repository) {
        this.repository = repository;
    }

    public List<String> catalog() {
        return repository.titles().stream().sorted().toList();
    }
}
