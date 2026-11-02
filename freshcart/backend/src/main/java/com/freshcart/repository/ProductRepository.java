package com.freshcart.repository;

import com.freshcart.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByStatus(String status);
    List<Product> findByNameContainingIgnoreCaseAndStatus(String name, String status);
}
