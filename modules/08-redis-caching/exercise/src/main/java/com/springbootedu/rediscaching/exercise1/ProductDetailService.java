package com.springbootedu.rediscaching.exercise1;

import java.math.BigDecimal;
import java.util.NoSuchElementException;
import org.springframework.stereotype.Service;

/**
 * Exercise 1 — product details are read often and change rarely: cache them, drop the entry on change.
 */
@Service
public class ProductDetailService {

    private final ProductCatalog catalog;

    public ProductDetailService(ProductCatalog catalog) {
        this.catalog = catalog;
    }

    // TODO 1a: cache the result in the "product-details" cache (key = id)
    public ProductDetail detail(String id) {
        ProductDetail product = catalog.find(id);
        if (product == null) {
            throw new NoSuchElementException("No product " + id);
        }
        return product;
    }

    // TODO 1b: remove only this product's entry from "product-details"
    public void changePrice(String id, BigDecimal newPrice) {
        catalog.changePrice(id, newPrice);                           // the next detail(id) reads the new price
    }
}
