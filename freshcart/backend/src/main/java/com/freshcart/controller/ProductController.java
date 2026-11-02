package com.freshcart.controller;

import com.freshcart.dto.ProductDto;
import com.freshcart.service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public ResponseEntity<List<ProductDto.ProductResponse>> getProducts(@RequestParam(required = false) String search) {
        return ResponseEntity.ok(productService.getAllProducts(search));
    }
}
