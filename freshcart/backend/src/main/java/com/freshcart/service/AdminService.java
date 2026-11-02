package com.freshcart.service;

import com.freshcart.model.*;
import com.freshcart.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class AdminService {
    private final ProductRepository productRepository;
    private final InventoryRepository inventoryRepository;
    private final PriceHistoryRepository priceHistoryRepository;
    private final StockMovementRepository stockMovementRepository;

    public AdminService(ProductRepository productRepository, InventoryRepository inventoryRepository, 
                       PriceHistoryRepository priceHistoryRepository, StockMovementRepository stockMovementRepository) {
        this.productRepository = productRepository;
        this.inventoryRepository = inventoryRepository;
        this.priceHistoryRepository = priceHistoryRepository;
        this.stockMovementRepository = stockMovementRepository;
    }

    @Transactional
    public void updatePrice(Long productId, BigDecimal newPrice, String username) {
        Product product = productRepository.findById(productId).orElseThrow(() -> new RuntimeException("Product not found"));
        BigDecimal oldPrice = product.getCurrentSellingPrice();
        if (newPrice == null || newPrice.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Invalid price");
        }
        product.setCurrentSellingPrice(newPrice);
        product.setUpdatedAt(LocalDateTime.now());
        productRepository.save(product);

        PriceHistory history = PriceHistory.builder()
                .product(product)
                .previousPrice(oldPrice)
                .newPrice(newPrice)
                .changedBy(username)
                .changedAt(LocalDateTime.now())
                .build();
        priceHistoryRepository.save(history);
    }

    @Transactional
    public void addStock(Long productId, BigDecimal quantity, String username) {
        if (quantity == null || quantity.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Invalid quantity");
        }
        Product product = productRepository.findById(productId).orElseThrow(() -> new RuntimeException("Product not found"));
        Inventory inventory = inventoryRepository.findByProduct(product)
                .orElseThrow(() -> new RuntimeException("Inventory not found"));
        inventory.setCurrentStock(inventory.getCurrentStock().add(quantity));
        inventory.setUpdatedAt(LocalDateTime.now());
        inventoryRepository.save(inventory);

        StockMovement movement = StockMovement.builder()
                .product(product)
                .movementType("ADD")
                .quantityChange(quantity)
                .operatorUsername(username)
                .reason("Stock replenishment")
                .createdAt(LocalDateTime.now())
                .build();
        stockMovementRepository.save(movement);
    }

    @Transactional
    public Product addProduct(String name, String category, String unit, BigDecimal price, 
                              BigDecimal initialStock, Integer minThreshold, MultipartFile image, String uploadDir) throws IOException {
        if (name == null || name.isBlank()) {
            throw new RuntimeException("Product name required");
        }
        if (productRepository.findByNameContainingIgnoreCaseAndStatus(name, "ACTIVE").stream()
                .anyMatch(p -> p.getName().equalsIgnoreCase(name))) {
            throw new RuntimeException("Product with this name already exists");
        }
        Product product = Product.builder()
                .name(name)
                .category(category)
                .unit(unit)
                .currentSellingPrice(price)
                .minimumStockThreshold(minThreshold != null ? minThreshold : 0)
                .status("ACTIVE")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        if (image != null && !image.isEmpty()) {
            String contentType = image.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                throw new RuntimeException("Invalid image type");
            }
            if (image.getSize() > 5 * 1024 * 1024) {
                throw new RuntimeException("Image too large");
            }
            Path uploadPath = Paths.get(uploadDir);
            Files.createDirectories(uploadPath);
            String filename = UUID.randomUUID().toString() + "_" + image.getOriginalFilename();
            Path filePath = uploadPath.resolve(filename);
            Files.copy(image.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            product.setImagePath("/uploads/" + filename);
        }

        Product saved = productRepository.save(product);
        Inventory inventory = Inventory.builder()
                .product(saved)
                .currentStock(initialStock != null ? initialStock : BigDecimal.ZERO)
                .updatedAt(LocalDateTime.now())
                .build();
        inventoryRepository.save(inventory);

        if (initialStock != null && initialStock.compareTo(BigDecimal.ZERO) > 0) {
            StockMovement movement = StockMovement.builder()
                    .product(saved)
                    .movementType("ADD")
                    .quantityChange(initialStock)
                    .reason("Initial stock")
                    .createdAt(LocalDateTime.now())
                    .build();
            stockMovementRepository.save(movement);
        }
        return saved;
    }

    @Transactional
    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id).orElseThrow(() -> new RuntimeException("Product not found"));
        product.setStatus("INACTIVE");
        productRepository.save(product);
    }
}
