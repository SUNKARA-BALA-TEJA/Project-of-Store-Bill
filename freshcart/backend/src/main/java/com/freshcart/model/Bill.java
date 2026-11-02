package com.freshcart.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "bills")
public class Bill {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "invoice_number", unique = true, nullable = false)
    private String invoiceNumber;

    @Column(name = "customer_name", nullable = false)
    private String customerName;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
    private String status = "COMPLETED";

    @Column(precision = 10, scale = 2)
    private BigDecimal subtotal = BigDecimal.ZERO;

    @Column(precision = 10, scale = 2)
    private BigDecimal charges = BigDecimal.ZERO;

    @Column(precision = 10, scale = 2)
    private BigDecimal discount = BigDecimal.ZERO;

    @Column(name = "final_total", nullable = false, precision = 10, scale = 2)
    private BigDecimal finalTotal = BigDecimal.ZERO;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "archived")
    private Boolean archived = false;

    @Column(name = "archived_at")
    private LocalDateTime archivedAt;

    @Column(name = "archived_by")
    private String archivedBy;

    @Column(name = "deleted_by")
    private String deletedBy;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @OneToMany(mappedBy = "bill", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BillItem> items = new ArrayList<>();

    public Bill() {}

    public Bill(Long id, String invoiceNumber, String customerName, User user, String status, BigDecimal subtotal, BigDecimal charges, BigDecimal discount, BigDecimal finalTotal, LocalDateTime createdAt, LocalDateTime completedAt, Boolean archived, LocalDateTime archivedAt, String archivedBy, String deletedBy, LocalDateTime deletedAt, List<BillItem> items) {
        this.id = id;
        this.invoiceNumber = invoiceNumber;
        this.customerName = customerName;
        this.user = user;
        this.status = status;
        this.subtotal = subtotal;
        this.charges = charges;
        this.discount = discount;
        this.finalTotal = finalTotal;
        this.createdAt = createdAt;
        this.completedAt = completedAt;
        this.items = items != null ? items : new ArrayList<>();
    }

    public static BillBuilder builder() {
        return new BillBuilder();
    }

    public static class BillBuilder {
        private Long id;
        private String invoiceNumber;
        private String customerName;
        private User user;
        private String status = "COMPLETED";
        private BigDecimal subtotal = BigDecimal.ZERO;
        private BigDecimal charges = BigDecimal.ZERO;
        private BigDecimal discount = BigDecimal.ZERO;
        private BigDecimal finalTotal = BigDecimal.ZERO;
        private LocalDateTime createdAt = LocalDateTime.now();
        private LocalDateTime completedAt;
        private Boolean archived = false;
        private LocalDateTime archivedAt;
        private String archivedBy;
        private String deletedBy;
        private LocalDateTime deletedAt;
        private List<BillItem> items = new ArrayList<>();

        public BillBuilder id(Long id) { this.id = id; return this; }
        public BillBuilder invoiceNumber(String invoiceNumber) { this.invoiceNumber = invoiceNumber; return this; }
        public BillBuilder customerName(String customerName) { this.customerName = customerName; return this; }
        public BillBuilder user(User user) { this.user = user; return this; }
        public BillBuilder status(String status) { this.status = status; return this; }
        public BillBuilder subtotal(BigDecimal subtotal) { this.subtotal = subtotal; return this; }
        public BillBuilder charges(BigDecimal charges) { this.charges = charges; return this; }
        public BillBuilder discount(BigDecimal discount) { this.discount = discount; return this; }
        public BillBuilder finalTotal(BigDecimal finalTotal) { this.finalTotal = finalTotal; return this; }
        public BillBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public BillBuilder completedAt(LocalDateTime completedAt) { this.completedAt = completedAt; return this; }
        public BillBuilder items(List<BillItem> items) { this.items = items; return this; }
        public Bill build() {
            return new Bill(id, invoiceNumber, customerName, user, status, subtotal, charges, discount, finalTotal, createdAt, completedAt, archived, archivedAt, archivedBy, deletedBy, deletedAt, items);
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getInvoiceNumber() { return invoiceNumber; }
    public void setInvoiceNumber(String invoiceNumber) { this.invoiceNumber = invoiceNumber; }
    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public BigDecimal getSubtotal() { return subtotal; }
    public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }
    public BigDecimal getCharges() { return charges; }
    public void setCharges(BigDecimal charges) { this.charges = charges; }
    public BigDecimal getDiscount() { return discount; }
    public void setDiscount(BigDecimal discount) { this.discount = discount; }
    public BigDecimal getFinalTotal() { return finalTotal; }
    public void setFinalTotal(BigDecimal finalTotal) { this.finalTotal = finalTotal; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getCompletedAt() { return completedAt; }
    public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }
    public Boolean getArchived() { return archived; }
    public void setArchived(Boolean archived) { this.archived = archived; }
    public LocalDateTime getArchivedAt() { return archivedAt; }
    public void setArchivedAt(LocalDateTime archivedAt) { this.archivedAt = archivedAt; }
    public String getArchivedBy() { return archivedBy; }
    public void setArchivedBy(String archivedBy) { this.archivedBy = archivedBy; }
    public String getDeletedBy() { return deletedBy; }
    public void setDeletedBy(String deletedBy) { this.deletedBy = deletedBy; }
    public LocalDateTime getDeletedAt() { return deletedAt; }
    public void setDeletedAt(LocalDateTime deletedAt) { this.deletedAt = deletedAt; }
    public List<BillItem> getItems() { return items; }
    public void setItems(List<BillItem> items) { this.items = items; }
}
