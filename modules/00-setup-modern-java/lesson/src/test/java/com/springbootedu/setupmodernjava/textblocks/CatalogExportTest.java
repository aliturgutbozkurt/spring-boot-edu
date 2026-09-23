package com.springbootedu.setupmodernjava.textblocks;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.Test;

/**
 * Lesson 3.4 — text blocks and var.
 */
class CatalogExportTest {

    @Test
    void rendersJsonWithATextBlock() {
        assertThat(CatalogExport.toJson("Effective Java", "Joshua Bloch", 2018)).isEqualTo("""
                {
                  "title": "Effective Java",
                  "author": "Joshua Bloch",
                  "year": 2018
                }""");
    }

    @Test
    void rendersAReceipt() {
        assertThat(CatalogExport.receipt(List.of("Effective Java", "Java Puzzlers")))
                .isEqualTo("KİTAPÇI / BOOKSTORE\n1. Effective Java\n2. Java Puzzlers\nToplam / Total: 2 kitap / books\n");
    }
}
