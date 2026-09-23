package com.springbootedu.datamongodb.exercise1;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.springbootedu.datamongodb.TestcontainersConfiguration;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.mongodb.test.autoconfigure.DataMongoTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DuplicateKeyException;

/**
 * Exercise 1 — products with different attributes in one collection.
 */
@DataMongoTest
@Import(TestcontainersConfiguration.class)
class Exercise1Test {

    @Autowired
    ProductRepository products;

    @BeforeEach
    void seed() {
        products.deleteAll();
        products.saveAll(List.of(
                new Product(null, "BOOK-1", "Effective Java", "book", new BigDecimal("89.90"),
                        Map.of("pages", 412, "language", "en")),
                new Product(null, "BAG-1", "Kanvas Çanta", "bag", new BigDecimal("150.00"),
                        Map.of("color", "red", "material", "canvas")),
                new Product(null, "BAG-2", "Deri Çanta", "bag", new BigDecimal("450.00"),
                        Map.of("color", "brown", "material", "leather")),
                new Product(null, "PEN-1", "Dolma Kalem", "pen", new BigDecimal("120.00"), Map.of("color", "red"))));
    }

    @Test
    void savesDocumentsWithDifferentAttributes() {
        assertThat(products.findBySku("BOOK-1").orElseThrow().attributes()).containsEntry("pages", 412);
        assertThat(products.findBySku("BAG-2").orElseThrow().attributes()).containsEntry("material", "leather");
    }

    @Test
    void productsOfACategorySortedByPrice() {
        assertThat(products.findByCategoryOrderByPrice("bag")).extracting(Product::sku).containsExactly("BAG-1", "BAG-2");
    }

    @Test
    void queryOnAnAttributeInsideTheMap() {
        assertThat(products.findByColor("red")).extracting(Product::sku).containsExactlyInAnyOrder("BAG-1", "PEN-1");
    }

    @Test
    void theSkuIsUnique() {
        assertThatThrownBy(() -> products.save(new Product(null, "PEN-1", "Kopya", "pen", BigDecimal.ONE, Map.of())))
                .isInstanceOf(DuplicateKeyException.class);
    }
}
