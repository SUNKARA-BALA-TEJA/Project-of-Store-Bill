package com.freshcart.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String category;

    @Column(name = "image_path")
    private String imagePath;

    @Column(nullable = false)
    private String unit;

    @Column(name = "current_selling_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal currentSellingPrice;

    @Column(name = "minimum_stock_threshold")
    private Integer minimumStockThreshold = 0;

    @Column(nullable = false)
    private String status = "ACTIVE";

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    public Product() {}

    public Product(Long id, String name, String category, String imagePath, String unit, BigDecimal currentSellingPrice, Integer minimumStockThreshold, String status, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.imagePath = imagePath;
        this.unit = unit;
        this.currentSellingPrice = currentSellingPrice;
        this.minimumStockThreshold = minimumStockThreshold;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static ProductBuilder builder() {
        return new ProductBuilder();
    }

    public static class ProductBuilder {
        private Long id;
        private String name;
        private String category;
        private String imagePath;
        private String unit;
        private BigDecimal currentSellingPrice;
        private Integer minimumStockThreshold = 0;
        private String status = "ACTIVE";
        private LocalDateTime createdAt = LocalDateTime.now();
        private LocalDateTime updatedAt = LocalDateTime.now();

        public ProductBuilder id(Long id) { this.id = id; return this; }
        public ProductBuilder name(String name) { this.name = name; return this; }
        public ProductBuilder category(String category) { this.category = category; return this; }
        public ProductBuilder imagePath(String imagePath) { this.imagePath = imagePath; return this; }
        public ProductBuilder unit(String unit) { this.unit = unit; return this; }
        public ProductBuilder currentSellingPrice(BigDecimal currentSellingPrice) { this.currentSellingPrice = currentSellingPrice; return this; }
        public ProductBuilder minimumStockThreshold(Integer minimumStockThreshold) { this.minimumStockThreshold = minimumStockThreshold; return this; }
        public ProductBuilder status(String status) { this.status = status; return this; }
        public ProductBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public ProductBuilder updatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; return this; }
        public Product build() {
            return new Product(id, name, category, imagePath, unit, currentSellingPrice, minimumStockThreshold, status, createdAt, updatedAt);
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getImagePath() { return imagePath; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }
    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }
    public BigDecimal getCurrentSellingPrice() { return currentSellingPrice; }
    public void setCurrentSellingPrice(BigDecimal currentSellingPrice) { this.currentSellingPrice = currentSellingPrice; }
    public Integer getMinimumStockThreshold() { return minimumStockThreshold; }
    public void setMinimumStockThreshold(Integer minimumStockThreshold) { this.minimumStockThreshold = minimumStockThreshold; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
