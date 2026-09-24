package com.springbootedu.testing.exercise3.legacy;

import com.springbootedu.testing.exercise3.shop.repository.ShopRepository;

/**
 * Given: an old controller that skips the service layer — your rules must catch it.
 */
public class LegacyReportController {

    private final ShopRepository repository = new ShopRepository();

    public int bookCount() {
        return repository.titles().size();
    }
}
