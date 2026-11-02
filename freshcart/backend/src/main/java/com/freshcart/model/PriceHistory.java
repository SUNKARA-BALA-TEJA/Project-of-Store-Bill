package com.freshcart.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "price_history")
public class PriceHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "previous_price", precision = 10, scale = 2)
    private BigDecimal previousPrice;

    @Column(name = "new_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal newPrice;

    @Column(name = "changed_by")
    private String changedBy;

    @Column(name = "changed_at", nullable = false)
    private LocalDateTime changedAt = LocalDateTime.now();

    public PriceHistory() {}

    public PriceHistory(Long id, Product product, BigDecimal previousPrice, BigDecimal newPrice, String changedBy, LocalDateTime changedAt) {
        this.id = id;
        this.product = product;
        this.previousPrice = previousPrice;
        this.newPrice = newPrice;
        this.changedBy = changedBy;
        this.changedAt = changedAt;
    }

    public static PriceHistoryBuilder builder() {
        return new PriceHistoryBuilder();
    }

    public static class PriceHistoryBuilder {
        private Long id;
        private Product product;
        private BigDecimal previousPrice;
        private BigDecimal newPrice;
        private String changedBy;
        private LocalDateTime changedAt = LocalDateTime.now();

        public PriceHistoryBuilder id(Long id) { this.id = id; return this; }
        public PriceHistoryBuilder product(Product product) { this.product = product; return this; }
        public PriceHistoryBuilder previousPrice(BigDecimal previousPrice) { this.previousPrice = previousPrice; return this; }
        public PriceHistoryBuilder newPrice(BigDecimal newPrice) { this.newPrice = newPrice; return this; }
        public PriceHistoryBuilder changedBy(String changedBy) { this.changedBy = changedBy; return this; }
        public PriceHistoryBuilder changedAt(LocalDateTime changedAt) { this.changedAt = changedAt; return this; }
        public PriceHistory build() {
            return new PriceHistory(id, product, previousPrice, newPrice, changedBy, changedAt);
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }
    public BigDecimal getPreviousPrice() { return previousPrice; }
    public void setPreviousPrice(BigDecimal previousPrice) { this.previousPrice = previousPrice; }
    public BigDecimal getNewPrice() { return newPrice; }
    public void setNewPrice(BigDecimal newPrice) { this.newPrice = newPrice; }
    public String getChangedBy() { return changedBy; }
    public void setChangedBy(String changedBy) { this.changedBy = changedBy; }
    public LocalDateTime getChangedAt() { return changedAt; }
    public void setChangedAt(LocalDateTime changedAt) { this.changedAt = changedAt; }
}
