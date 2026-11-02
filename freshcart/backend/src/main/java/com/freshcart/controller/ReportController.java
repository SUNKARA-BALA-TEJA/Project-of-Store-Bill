package com.freshcart.controller;

import com.freshcart.model.Bill;
import com.freshcart.repository.BillRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/reports")
public class ReportController {
    private final BillRepository billRepository;

    public ReportController(BillRepository billRepository) {
        this.billRepository = billRepository;
    }

    @GetMapping("/daily")
    public ResponseEntity<?> dailyReport(@RequestParam String date) {
        LocalDate localDate = LocalDate.parse(date);
        LocalDateTime start = localDate.atStartOfDay();
        LocalDateTime end = localDate.atTime(LocalTime.MAX);
        List<Bill> bills = billRepository.findByCreatedAtBetweenOrderByCreatedAtDesc(start, end);
        BigDecimal totalSales = bills.stream().map(Bill::getFinalTotal).reduce(BigDecimal.ZERO, BigDecimal::add);
        return ResponseEntity.ok(Map.of(
                "date", date,
                "totalSales", totalSales,
                "billCount", bills.size(),
                "bills", bills.stream().map(b -> Map.of(
                        "invoiceNumber", b.getInvoiceNumber(),
                        "customerName", b.getCustomerName(),
                        "finalTotal", b.getFinalTotal(),
                        "createdAt", b.getCreatedAt()
                )).collect(Collectors.toList())
        ));
    }

    @GetMapping("/summary")
    public ResponseEntity<?> summary() {
        List<Bill> allBills = billRepository.findAll();
        BigDecimal totalSales = allBills.stream().map(Bill::getFinalTotal).reduce(BigDecimal.ZERO, BigDecimal::add);
        return ResponseEntity.ok(Map.of(
                "totalBills", allBills.size(),
                "totalRevenue", totalSales
        ));
    }
}
