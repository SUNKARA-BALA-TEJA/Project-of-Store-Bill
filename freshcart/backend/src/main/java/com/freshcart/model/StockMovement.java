package com.freshcart.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "stock_movements")
public class StockMovement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "movement_type", nullable = false)
    private String movementType;

    @Column(name = "quantity_change", nullable = false, precision = 10, scale = 3)
    private BigDecimal quantityChange;

    @Column(name = "operator_username")
    private String operatorUsername;

    @Column(name = "related_invoice_number")
    private String relatedInvoiceNumber;

    private String reason;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public StockMovement() {}

    public StockMovement(Long id, Product product, String movementType, BigDecimal quantityChange, String operatorUsername, String relatedInvoiceNumber, String reason, LocalDateTime createdAt) {
        this.id = id;
        this.product = product;
        this.movementType = movementType;
        this.quantityChange = quantityChange;
        this.operatorUsername = operatorUsername;
        this.relatedInvoiceNumber = relatedInvoiceNumber;
        this.reason = reason;
        this.createdAt = createdAt;
    }

    public static StockMovementBuilder builder() {
        return new StockMovementBuilder();
    }

    public static class StockMovementBuilder {
        private Long id;
        private Product product;
        private String movementType;
        private BigDecimal quantityChange;
        private String operatorUsername;
        private String relatedInvoiceNumber;
        private String reason;
        private LocalDateTime createdAt = LocalDateTime.now();

        public StockMovementBuilder id(Long id) { this.id = id; return this; }
        public StockMovementBuilder product(Product product) { this.product = product; return this; }
        public StockMovementBuilder movementType(String movementType) { this.movementType = movementType; return this; }
        public StockMovementBuilder quantityChange(BigDecimal quantityChange) { this.quantityChange = quantityChange; return this; }
        public StockMovementBuilder operatorUsername(String operatorUsername) { this.operatorUsername = operatorUsername; return this; }
        public StockMovementBuilder relatedInvoiceNumber(String relatedInvoiceNumber) { this.relatedInvoiceNumber = relatedInvoiceNumber; return this; }
        public StockMovementBuilder reason(String reason) { this.reason = reason; return this; }
        public StockMovementBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public StockMovement build() {
            return new StockMovement(id, product, movementType, quantityChange, operatorUsername, relatedInvoiceNumber, reason, createdAt);
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }
    public String getMovementType() { return movementType; }
    public void setMovementType(String movementType) { this.movementType = movementType; }
    public BigDecimal getQuantityChange() { return quantityChange; }
    public void setQuantityChange(BigDecimal quantityChange) { this.quantityChange = quantityChange; }
    public String getOperatorUsername() { return operatorUsername; }
    public void setOperatorUsername(String operatorUsername) { this.operatorUsername = operatorUsername; }
    public String getRelatedInvoiceNumber() { return relatedInvoiceNumber; }
    public void setRelatedInvoiceNumber(String relatedInvoiceNumber) { this.relatedInvoiceNumber = relatedInvoiceNumber; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
