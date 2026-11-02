package com.freshcart.controller;

import com.freshcart.service.AdminService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    private final AdminService adminService;
    
    @Value("${app.upload.dir:./uploads}")
    private String uploadDir;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @PostMapping("/price")
    public ResponseEntity<?> updatePrice(@RequestParam Long productId, @RequestParam BigDecimal newPrice, Authentication authentication) {
        try {
            adminService.updatePrice(productId, newPrice, authentication.getName());
            return ResponseEntity.ok(Map.of("message", "Price updated successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/stock/add")
    public ResponseEntity<?> addStock(@RequestParam Long productId, @RequestParam BigDecimal quantity, Authentication authentication) {
        try {
            adminService.addStock(productId, quantity, authentication.getName());
            return ResponseEntity.ok(Map.of("message", "Stock added successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/product")
    public ResponseEntity<?> addProduct(
            @RequestParam String name,
            @RequestParam(required = false) String category,
            @RequestParam String unit,
            @RequestParam BigDecimal price,
            @RequestParam(required = false) BigDecimal initialStock,
            @RequestParam(required = false) Integer minThreshold,
            @RequestParam(required = false) MultipartFile image) {
        try {
            return ResponseEntity.ok(adminService.addProduct(name, category, unit, price, initialStock, minThreshold, image, uploadDir));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/product/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable Long id) {
        try {
            adminService.deleteProduct(id);
            return ResponseEntity.ok(Map.of("message", "Product deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
