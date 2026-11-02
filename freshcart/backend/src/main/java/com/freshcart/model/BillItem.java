package com.freshcart.model;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "bill_items")
public class BillItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "bill_id", nullable = false)
    private Bill bill;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @Column(name = "product_name_snapshot", nullable = false)
    private String productNameSnapshot;

    @Column(name = "unit_snapshot", nullable = false)
    private String unitSnapshot;

    @Column(nullable = false, precision = 10, scale = 3)
    private BigDecimal quantity;

    @Column(name = "actual_unit_price_charged", nullable = false, precision = 10, scale = 2)
    private BigDecimal actualUnitPriceCharged;

    @Column(name = "line_subtotal", nullable = false, precision = 10, scale = 2)
    private BigDecimal lineSubtotal;

    public BillItem() {}

    public BillItem(Long id, Bill bill, Product product, String productNameSnapshot, String unitSnapshot, BigDecimal quantity, BigDecimal actualUnitPriceCharged, BigDecimal lineSubtotal) {
        this.id = id;
        this.bill = bill;
        this.product = product;
        this.productNameSnapshot = productNameSnapshot;
        this.unitSnapshot = unitSnapshot;
        this.quantity = quantity;
        this.actualUnitPriceCharged = actualUnitPriceCharged;
        this.lineSubtotal = lineSubtotal;
    }

    public static BillItemBuilder builder() {
        return new BillItemBuilder();
    }

    public static class BillItemBuilder {
        private Long id;
        private Bill bill;
        private Product product;
        private String productNameSnapshot;
        private String unitSnapshot;
        private BigDecimal quantity;
        private BigDecimal actualUnitPriceCharged;
        private BigDecimal lineSubtotal;

        public BillItemBuilder id(Long id) { this.id = id; return this; }
        public BillItemBuilder bill(Bill bill) { this.bill = bill; return this; }
        public BillItemBuilder product(Product product) { this.product = product; return this; }
        public BillItemBuilder productNameSnapshot(String productNameSnapshot) { this.productNameSnapshot = productNameSnapshot; return this; }
        public BillItemBuilder unitSnapshot(String unitSnapshot) { this.unitSnapshot = unitSnapshot; return this; }
        public BillItemBuilder quantity(BigDecimal quantity) { this.quantity = quantity; return this; }
        public BillItemBuilder actualUnitPriceCharged(BigDecimal actualUnitPriceCharged) { this.actualUnitPriceCharged = actualUnitPriceCharged; return this; }
        public BillItemBuilder lineSubtotal(BigDecimal lineSubtotal) { this.lineSubtotal = lineSubtotal; return this; }
        public BillItem build() {
            return new BillItem(id, bill, product, productNameSnapshot, unitSnapshot, quantity, actualUnitPriceCharged, lineSubtotal);
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Bill getBill() { return bill; }
    public void setBill(Bill bill) { this.bill = bill; }
    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }
    public String getProductNameSnapshot() { return productNameSnapshot; }
    public void setProductNameSnapshot(String productNameSnapshot) { this.productNameSnapshot = productNameSnapshot; }
    public String getUnitSnapshot() { return unitSnapshot; }
    public void setUnitSnapshot(String unitSnapshot) { this.unitSnapshot = unitSnapshot; }
    public BigDecimal getQuantity() { return quantity; }
    public void setQuantity(BigDecimal quantity) { this.quantity = quantity; }
    public BigDecimal getActualUnitPriceCharged() { return actualUnitPriceCharged; }
    public void setActualUnitPriceCharged(BigDecimal actualUnitPriceCharged) { this.actualUnitPriceCharged = actualUnitPriceCharged; }
    public BigDecimal getLineSubtotal() { return lineSubtotal; }
    public void setLineSubtotal(BigDecimal lineSubtotal) { this.lineSubtotal = lineSubtotal; }
}
