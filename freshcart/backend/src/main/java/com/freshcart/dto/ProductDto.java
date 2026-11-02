package com.freshcart.dto;

import java.math.BigDecimal;

public class ProductDto {
    public static class ProductResponse {
        private Long id;
        private String name;
        private String category;
        private String imagePath;
        private String unit;
        private BigDecimal currentSellingPrice;
        private BigDecimal stock;
        private Integer minimumStockThreshold;
        private String status;

        public ProductResponse() {}

        public ProductResponse(Long id, String name, String category, String imagePath, String unit, BigDecimal currentSellingPrice, BigDecimal stock, Integer minimumStockThreshold, String status) {
            this.id = id;
            this.name = name;
            this.category = category;
            this.imagePath = imagePath;
            this.unit = unit;
            this.currentSellingPrice = currentSellingPrice;
            this.stock = stock;
            this.minimumStockThreshold = minimumStockThreshold;
            this.status = status;
        }

        public static ProductResponseBuilder builder() {
            return new ProductResponseBuilder();
        }

        public static class ProductResponseBuilder {
            private Long id;
            private String name;
            private String category;
            private String imagePath;
            private String unit;
            private BigDecimal currentSellingPrice;
            private BigDecimal stock;
            private Integer minimumStockThreshold;
            private String status;

            public ProductResponseBuilder id(Long id) { this.id = id; return this; }
            public ProductResponseBuilder name(String name) { this.name = name; return this; }
            public ProductResponseBuilder category(String category) { this.category = category; return this; }
            public ProductResponseBuilder imagePath(String imagePath) { this.imagePath = imagePath; return this; }
            public ProductResponseBuilder unit(String unit) { this.unit = unit; return this; }
            public ProductResponseBuilder currentSellingPrice(BigDecimal currentSellingPrice) { this.currentSellingPrice = currentSellingPrice; return this; }
            public ProductResponseBuilder stock(BigDecimal stock) { this.stock = stock; return this; }
            public ProductResponseBuilder minimumStockThreshold(Integer minimumStockThreshold) { this.minimumStockThreshold = minimumStockThreshold; return this; }
            public ProductResponseBuilder status(String status) { this.status = status; return this; }
            public ProductResponse build() {
                return new ProductResponse(id, name, category, imagePath, unit, currentSellingPrice, stock, minimumStockThreshold, status);
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
        public BigDecimal getStock() { return stock; }
        public void setStock(BigDecimal stock) { this.stock = stock; }
        public Integer getMinimumStockThreshold() { return minimumStockThreshold; }
        public void setMinimumStockThreshold(Integer minimumStockThreshold) { this.minimumStockThreshold = minimumStockThreshold; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
    }
}
