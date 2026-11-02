package com.freshcart.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class BillDto {
    public static class BillItemRequest {
        private Long productId;
        private BigDecimal quantity;

        public Long getProductId() { return productId; }
        public void setProductId(Long productId) { this.productId = productId; }
        public BigDecimal getQuantity() { return quantity; }
        public void setQuantity(BigDecimal quantity) { this.quantity = quantity; }
    }

    public static class CreateBillRequest {
        private String customerName;
        private List<BillItemRequest> items;
        private BigDecimal charges;
        private BigDecimal discount;

        public String getCustomerName() { return customerName; }
        public void setCustomerName(String customerName) { this.customerName = customerName; }
        public List<BillItemRequest> getItems() { return items; }
        public void setItems(List<BillItemRequest> items) { this.items = items; }
        public BigDecimal getCharges() { return charges; }
        public void setCharges(BigDecimal charges) { this.charges = charges; }
        public BigDecimal getDiscount() { return discount; }
        public void setDiscount(BigDecimal discount) { this.discount = discount; }
    }

    public static class BillItemResponse {
        private Long productId;
        private String productName;
        private String unit;
        private BigDecimal quantity;
        private BigDecimal unitPrice;
        private BigDecimal subtotal;
        private String imagePath;

        public BillItemResponse() {}

        public BillItemResponse(Long productId, String productName, String unit, BigDecimal quantity, BigDecimal unitPrice, BigDecimal subtotal, String imagePath) {
            this.productId = productId;
            this.productName = productName;
            this.unit = unit;
            this.quantity = quantity;
            this.unitPrice = unitPrice;
            this.subtotal = subtotal;
            this.imagePath = imagePath;
        }

        public static BillItemResponseBuilder builder() {
            return new BillItemResponseBuilder();
        }

        public static class BillItemResponseBuilder {
            private Long productId;
            private String productName;
            private String unit;
            private BigDecimal quantity;
            private BigDecimal unitPrice;
            private BigDecimal subtotal;
            private String imagePath;

            public BillItemResponseBuilder productId(Long productId) { this.productId = productId; return this; }
            public BillItemResponseBuilder productName(String productName) { this.productName = productName; return this; }
            public BillItemResponseBuilder unit(String unit) { this.unit = unit; return this; }
            public BillItemResponseBuilder quantity(BigDecimal quantity) { this.quantity = quantity; return this; }
            public BillItemResponseBuilder unitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; return this; }
            public BillItemResponseBuilder subtotal(BigDecimal subtotal) { this.subtotal = subtotal; return this; }
            public BillItemResponseBuilder imagePath(String imagePath) { this.imagePath = imagePath; return this; }
            public BillItemResponse build() {
                return new BillItemResponse(productId, productName, unit, quantity, unitPrice, subtotal, imagePath);
            }
        }

        public Long getProductId() { return productId; }
        public void setProductId(Long productId) { this.productId = productId; }
        public String getProductName() { return productName; }
        public void setProductName(String productName) { this.productName = productName; }
        public String getUnit() { return unit; }
        public void setUnit(String unit) { this.unit = unit; }
        public BigDecimal getQuantity() { return quantity; }
        public void setQuantity(BigDecimal quantity) { this.quantity = quantity; }
        public BigDecimal getUnitPrice() { return unitPrice; }
        public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }
        public BigDecimal getSubtotal() { return subtotal; }
        public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }
        public String getImagePath() { return imagePath; }
        public void setImagePath(String imagePath) { this.imagePath = imagePath; }
    }

    public static class BillResponse {
        private String invoiceNumber;
        private String customerName;
        private LocalDateTime createdAt;
        private BigDecimal subtotal;
        private BigDecimal charges;
        private BigDecimal discount;
        private BigDecimal finalTotal;
        private List<BillItemResponse> items;

        public BillResponse() {}

        public BillResponse(String invoiceNumber, String customerName, LocalDateTime createdAt, BigDecimal subtotal, BigDecimal charges, BigDecimal discount, BigDecimal finalTotal, List<BillItemResponse> items) {
            this.invoiceNumber = invoiceNumber;
            this.customerName = customerName;
            this.createdAt = createdAt;
            this.subtotal = subtotal;
            this.charges = charges;
            this.discount = discount;
            this.finalTotal = finalTotal;
            this.items = items;
        }

        public static BillResponseBuilder builder() {
            return new BillResponseBuilder();
        }

        public static class BillResponseBuilder {
            private String invoiceNumber;
            private String customerName;
            private LocalDateTime createdAt;
            private BigDecimal subtotal;
            private BigDecimal charges;
            private BigDecimal discount;
            private BigDecimal finalTotal;
            private List<BillItemResponse> items;

            public BillResponseBuilder invoiceNumber(String invoiceNumber) { this.invoiceNumber = invoiceNumber; return this; }
            public BillResponseBuilder customerName(String customerName) { this.customerName = customerName; return this; }
            public BillResponseBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
            public BillResponseBuilder subtotal(BigDecimal subtotal) { this.subtotal = subtotal; return this; }
            public BillResponseBuilder charges(BigDecimal charges) { this.charges = charges; return this; }
            public BillResponseBuilder discount(BigDecimal discount) { this.discount = discount; return this; }
            public BillResponseBuilder finalTotal(BigDecimal finalTotal) { this.finalTotal = finalTotal; return this; }
            public BillResponseBuilder items(List<BillItemResponse> items) { this.items = items; return this; }
            public BillResponse build() {
                return new BillResponse(invoiceNumber, customerName, createdAt, subtotal, charges, discount, finalTotal, items);
            }
        }

        public String getInvoiceNumber() { return invoiceNumber; }
        public void setInvoiceNumber(String invoiceNumber) { this.invoiceNumber = invoiceNumber; }
        public String getCustomerName() { return customerName; }
        public void setCustomerName(String customerName) { this.customerName = customerName; }
        public LocalDateTime getCreatedAt() { return createdAt; }
        public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
        public BigDecimal getSubtotal() { return subtotal; }
        public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }
        public BigDecimal getCharges() { return charges; }
        public void setCharges(BigDecimal charges) { this.charges = charges; }
        public BigDecimal getDiscount() { return discount; }
        public void setDiscount(BigDecimal discount) { this.discount = discount; }
        public BigDecimal getFinalTotal() { return finalTotal; }
        public void setFinalTotal(BigDecimal finalTotal) { this.finalTotal = finalTotal; }
        public List<BillItemResponse> getItems() { return items; }
        public void setItems(List<BillItemResponse> items) { this.items = items; }
    }

    public static class BillSummaryResponse {
        private String invoiceNumber;
        private LocalDateTime createdAt;
        private String customerName;
        private Integer itemCount;
        private BigDecimal finalTotal;
        private String status;

        public BillSummaryResponse() {}

        public BillSummaryResponse(String invoiceNumber, LocalDateTime createdAt, String customerName, Integer itemCount, BigDecimal finalTotal, String status) {
            this.invoiceNumber = invoiceNumber;
            this.createdAt = createdAt;
            this.customerName = customerName;
            this.itemCount = itemCount;
            this.finalTotal = finalTotal;
            this.status = status;
        }

        public static BillSummaryResponseBuilder builder() {
            return new BillSummaryResponseBuilder();
        }

        public static class BillSummaryResponseBuilder {
            private String invoiceNumber;
            private LocalDateTime createdAt;
            private String customerName;
            private Integer itemCount;
            private BigDecimal finalTotal;
            private String status;

            public BillSummaryResponseBuilder invoiceNumber(String invoiceNumber) { this.invoiceNumber = invoiceNumber; return this; }
            public BillSummaryResponseBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
            public BillSummaryResponseBuilder customerName(String customerName) { this.customerName = customerName; return this; }
            public BillSummaryResponseBuilder itemCount(Integer itemCount) { this.itemCount = itemCount; return this; }
            public BillSummaryResponseBuilder finalTotal(BigDecimal finalTotal) { this.finalTotal = finalTotal; return this; }
            public BillSummaryResponseBuilder status(String status) { this.status = status; return this; }
            public BillSummaryResponse build() {
                return new BillSummaryResponse(invoiceNumber, createdAt, customerName, itemCount, finalTotal, status);
            }
        }

        public String getInvoiceNumber() { return invoiceNumber; }
        public void setInvoiceNumber(String invoiceNumber) { this.invoiceNumber = invoiceNumber; }
        public LocalDateTime getCreatedAt() { return createdAt; }
        public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
        public String getCustomerName() { return customerName; }
        public void setCustomerName(String customerName) { this.customerName = customerName; }
        public Integer getItemCount() { return itemCount; }
        public void setItemCount(Integer itemCount) { this.itemCount = itemCount; }
        public BigDecimal getFinalTotal() { return finalTotal; }
        public void setFinalTotal(BigDecimal finalTotal) { this.finalTotal = finalTotal; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
    }
}
