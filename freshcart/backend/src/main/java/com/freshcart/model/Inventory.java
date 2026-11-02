package com.freshcart.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "inventory")
public class Inventory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "product_id", nullable = false, unique = true)
    private Product product;

    @Column(name = "current_stock", nullable = false, precision = 10, scale = 3)
    private BigDecimal currentStock = BigDecimal.ZERO;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    public Inventory() {}

    public Inventory(Long id, Product product, BigDecimal currentStock, LocalDateTime updatedAt) {
        this.id = id;
        this.product = product;
        this.currentStock = currentStock;
        this.updatedAt = updatedAt;
    }

    public static InventoryBuilder builder() {
        return new InventoryBuilder();
    }

    public static class InventoryBuilder {
        private Long id;
        private Product product;
        private BigDecimal currentStock = BigDecimal.ZERO;
        private LocalDateTime updatedAt = LocalDateTime.now();

        public InventoryBuilder id(Long id) { this.id = id; return this; }
        public InventoryBuilder product(Product product) { this.product = product; return this; }
        public InventoryBuilder currentStock(BigDecimal currentStock) { this.currentStock = currentStock; return this; }
        public InventoryBuilder updatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; return this; }
        public Inventory build() {
            return new Inventory(id, product, currentStock, updatedAt);
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }
    public BigDecimal getCurrentStock() { return currentStock; }
    public void setCurrentStock(BigDecimal currentStock) { this.currentStock = currentStock; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
