package com.freshcart.controller;

import com.freshcart.model.Bill;
import com.freshcart.model.BillItem;
import com.freshcart.repository.AuditLogRepository;
import com.freshcart.repository.BillRepository;
import jakarta.transaction.Transactional;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/bills")
@PreAuthorize("hasRole('ADMIN')")
public class AdminBillController {
    private final BillRepository billRepository;
    private final AuditLogRepository auditLogRepository;

    public AdminBillController(BillRepository billRepository, AuditLogRepository auditLogRepository) {
        this.billRepository = billRepository;
        this.auditLogRepository = auditLogRepository;
    }

    @GetMapping
    public ResponseEntity<?> getAllBills(@RequestParam(required = false) String invoiceNumber,
                                        @RequestParam(required = false) String customerName) {
        List<Bill> bills = billRepository.findAllByOrderByCreatedAtDesc();
        if (customerName != null && !customerName.isEmpty()) {
            bills = bills.stream().filter(b -> b.getCustomerName() != null && b.getCustomerName().toLowerCase().contains(customerName.toLowerCase())).collect(Collectors.toList());
        }
        if (invoiceNumber != null && !invoiceNumber.isEmpty()) {
            bills = bills.stream().filter(b -> b.getInvoiceNumber() != null && b.getInvoiceNumber().toLowerCase().contains(invoiceNumber.toLowerCase())).collect(Collectors.toList());
        }
        List<Map<String, Object>> result = bills.stream().map(b -> {
            Map<String, Object> map = new HashMap<>();
            map.put("billId", b.getInvoiceNumber());
            map.put("invoiceNumber", b.getInvoiceNumber());
            map.put("id", b.getInvoiceNumber());
            map.put("customerName", b.getCustomerName());
            map.put("billDate", b.getCreatedAt());
            map.put("createdAt", b.getCreatedAt());
            map.put("itemCount", b.getItems() != null ? b.getItems().size() : 0);
            int totalQty = 0;
            if (b.getItems() != null) {
                for (BillItem item : b.getItems()) {
                    totalQty += item.getQuantity() != null ? item.getQuantity().intValue() : 0;
                }
            }
            map.put("totalQuantity", totalQty);
            map.put("totalAmount", b.getFinalTotal());
            map.put("finalTotal", b.getFinalTotal());
            map.put("createdBy", b.getUser() != null ? b.getUser().getUsername() : null);
            map.put("status", b.getStatus());
            return map;
        }).collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getBill(@PathVariable String id) {
        Bill bill = billRepository.findByInvoiceNumber(id).orElseThrow(() -> new RuntimeException("Bill not found"));
        Map<String, Object> map = new HashMap<>();
        map.put("invoiceNumber", bill.getInvoiceNumber());
        map.put("customerName", bill.getCustomerName());
        map.put("finalTotal", bill.getFinalTotal());
        map.put("items", bill.getItems());
        map.put("createdAt", bill.getCreatedAt());
        return ResponseEntity.ok(map);
    }

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<?> deleteBill(@PathVariable String id, Authentication auth) {
        Bill bill = billRepository.findByInvoiceNumber(id).orElseThrow(() -> new RuntimeException("Bill not found"));
        try {
            if (bill.getItems() != null) {
                bill.getItems().clear();
            }
            billRepository.delete(bill);
            auditLogRepository.save(new com.freshcart.model.AuditLog("DELETE_BILL", auth.getName(), null, id, "Bill deleted"));
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete bill");
        }
        return ResponseEntity.ok(Map.of("message", "Bill deleted successfully"));
    }
}
