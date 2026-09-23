package com.springbootedu.datajpapostgres.catalog;

import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Version;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import org.jspecify.annotations.Nullable;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * Lessons 3.1 and 3.6 — an entity: a mutable class with an identity, managed by Hibernate.
 */
// tag::book[]
@Entity
@EntityListeners(AuditingEntityListener.class)             // fills @CreatedDate / @LastModifiedDate
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private @Nullable Long id;

    private String isbn;
    private String title;
    private BigDecimal price;
    private int stock;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)    // default for @ManyToOne is EAGER — avoid it
    @JoinColumn(name = "author_id")
    private Author author;

    @ManyToMany
    @JoinTable(name = "book_category",
            joinColumns = @JoinColumn(name = "book_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id"))
    private Set<Category> categories = new HashSet<>();

    @CreatedDate
    private @Nullable Instant createdAt;

    @LastModifiedDate
    private @Nullable Instant updatedAt;

    @Version                                                // optimistic locking
    private @Nullable Long version;
    // end::book[]

    protected Book() {                                      // required by JPA; Hibernate fills the fields
        this.isbn = "";
        this.title = "";
        this.price = BigDecimal.ZERO;
        this.author = new Author("");
    }

    public Book(String isbn, String title, Author author, BigDecimal price, int stock) {
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.price = price;
        this.stock = stock;
    }

    // tag::behaviour[]
    public void changePrice(BigDecimal newPrice) {          // behaviour instead of setters
        if (newPrice.signum() < 0) {
            throw new IllegalArgumentException("Price must not be negative");
        }
        this.price = newPrice;
    }

    public void removeStock(int quantity) {
        if (quantity > stock) {
            throw new IllegalStateException("Only " + stock + " left");
        }
        this.stock -= quantity;                              // no save() needed: dirty checking at commit
    }
    // end::behaviour[]

    public @Nullable Long getId() {
        return id;
    }

    public String getIsbn() {
        return isbn;
    }

    public String getTitle() {
        return title;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public int getStock() {
        return stock;
    }

    public Author getAuthor() {
        return author;
    }

    public Set<Category> getCategories() {
        return categories;
    }

    public @Nullable Instant getCreatedAt() {
        return createdAt;
    }

    public @Nullable Instant getUpdatedAt() {
        return updatedAt;
    }

    public @Nullable Long getVersion() {
        return version;
    }

    // Equality by the natural key: stable before and after the database assigns the id.
    @Override
    public boolean equals(Object other) {
        return this == other || (other instanceof Book book && isbn.equals(book.isbn));
    }

    @Override
    public int hashCode() {
        return Objects.hash(isbn);
    }
}
