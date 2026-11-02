package com.freshcart.controller;

import com.freshcart.dto.BillDto;
import com.freshcart.service.BillingService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/bills")
public class BillingController {
    private final BillingService billingService;

    public BillingController(BillingService billingService) {
        this.billingService = billingService;
    }

    @PostMapping
    public ResponseEntity<?> createBill(@RequestBody BillDto.CreateBillRequest request, Authentication authentication) {
        try {
            String username = authentication != null ? authentication.getName() : null;
            return ResponseEntity.ok(billingService.createBill(request, username));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<List<BillDto.BillSummaryResponse>> getAllBills() {
        return ResponseEntity.ok(billingService.getAllBills());
    }

    @GetMapping("/{invoiceNumber}")
    public ResponseEntity<?> getBill(@PathVariable String invoiceNumber) {
        try {
            return ResponseEntity.ok(billingService.getBill(invoiceNumber));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
