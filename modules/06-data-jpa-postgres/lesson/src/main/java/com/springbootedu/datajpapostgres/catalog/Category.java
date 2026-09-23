package com.springbootedu.datajpapostgres.catalog;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import org.jspecify.annotations.Nullable;

/**
 * Lesson 3.1 — the other side of book N—N category.
 */
@Entity
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private @Nullable Long id;

    private String name;

    protected Category() {
        this.name = "";
    }

    public Category(String name) {
        this.name = name;
    }

    public @Nullable Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
