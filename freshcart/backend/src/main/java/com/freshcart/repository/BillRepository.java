package com.freshcart.repository;

import com.freshcart.model.Bill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BillRepository extends JpaRepository<Bill, Long> {
    Optional<Bill> findByInvoiceNumber(String invoiceNumber);
    List<Bill> findByCreatedAtBetweenOrderByCreatedAtDesc(LocalDateTime start, LocalDateTime end);
    List<Bill> findAllByOrderByCreatedAtDesc();
    List<Bill> findAllByArchivedFalseOrderByCreatedAtDesc();
    List<Bill> findAllByArchivedTrueOrderByArchivedAtDesc();
    
    @Query("SELECT b FROM Bill b WHERE LOWER(b.customerName) LIKE LOWER(CONCAT('%', :name, '%')) ORDER BY b.createdAt DESC")
    List<Bill> findByCustomerNameContaining(@Param("name") String name);
}
