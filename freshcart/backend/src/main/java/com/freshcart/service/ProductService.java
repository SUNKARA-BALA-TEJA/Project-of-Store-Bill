package com.freshcart.service;

import com.freshcart.dto.ProductDto;
import com.freshcart.model.Inventory;
import com.freshcart.model.Product;
import com.freshcart.repository.InventoryRepository;
import com.freshcart.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductService {
    private final ProductRepository productRepository;
    private final InventoryRepository inventoryRepository;

    public ProductService(ProductRepository productRepository, InventoryRepository inventoryRepository) {
        this.productRepository = productRepository;
        this.inventoryRepository = inventoryRepository;
    }

    public List<ProductDto.ProductResponse> getAllProducts(String search) {
        List<Product> products;
        if (search != null && !search.isBlank()) {
            products = productRepository.findByNameContainingIgnoreCaseAndStatus(search, "ACTIVE");
        } else {
            products = productRepository.findByStatus("ACTIVE");
        }
        return products.stream().map(p -> {
            BigDecimal stock = inventoryRepository.findByProduct(p)
                    .map(Inventory::getCurrentStock)
                    .orElse(BigDecimal.ZERO);
            return ProductDto.ProductResponse.builder()
                    .id(p.getId())
                    .name(p.getName())
                    .category(p.getCategory())
                    .imagePath(p.getImagePath())
                    .unit(p.getUnit())
                    .currentSellingPrice(p.getCurrentSellingPrice())
                    .stock(stock)
                    .minimumStockThreshold(p.getMinimumStockThreshold())
                    .status(p.getStatus())
                    .build();
        }).collect(Collectors.toList());
    }

    public Product getProduct(Long id) {
        return productRepository.findById(id).orElseThrow(() -> new RuntimeException("Product not found"));
    }
}
