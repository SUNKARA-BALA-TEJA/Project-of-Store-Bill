package com.freshcart.controller;

import com.freshcart.model.Bill;
import com.freshcart.repository.AuditLogRepository;
import com.freshcart.repository.BillRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/data")
@PreAuthorize("hasRole('ADMIN')")
public class DataManagementController {
    private final BillRepository billRepository;
    private final AuditLogRepository auditLogRepository;

    public DataManagementController(BillRepository billRepository, AuditLogRepository auditLogRepository) {
        this.billRepository = billRepository;
        this.auditLogRepository = auditLogRepository;
    }

    @GetMapping("/bills")
    public ResponseEntity<?> getBills(@RequestParam(required = false) String customerName, 
                                      @RequestParam(required = false) String invoiceNumber,
                                      @RequestParam(required = false) String archived) {
        List<Bill> bills;
        boolean showArchived = "true".equals(archived);
        if (showArchived) {
            bills = billRepository.findAllByArchivedTrueOrderByArchivedAtDesc();
        } else {
            bills = billRepository.findAllByArchivedFalseOrderByCreatedAtDesc();
        }
        if (customerName != null && !customerName.isEmpty()) {
            bills = bills.stream().filter(b -> b.getCustomerName().toLowerCase().contains(customerName.toLowerCase())).collect(Collectors.toList());
        }
        if (invoiceNumber != null && !invoiceNumber.isEmpty()) {
            bills = bills.stream().filter(b -> b.getInvoiceNumber().toLowerCase().contains(invoiceNumber.toLowerCase())).collect(Collectors.toList());
        }
        List<Map<String, Object>> result = bills.stream().map(b -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", b.getId());
            map.put("invoiceNumber", b.getInvoiceNumber());
            map.put("customerName", b.getCustomerName());
            map.put("finalTotal", b.getFinalTotal());
            map.put("createdAt", b.getCreatedAt());
            map.put("status", b.getStatus());
            map.put("archived", b.getArchived());
            map.put("archivedAt", b.getArchivedAt());
            map.put("archivedBy", b.getArchivedBy());
            map.put("itemCount", b.getItems() != null ? b.getItems().size() : 0);
            return map;
        }).collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }

    @GetMapping("/bills/{invoiceNumber}")
    public ResponseEntity<?> getBillDetails(@PathVariable String invoiceNumber) {
        Bill bill = billRepository.findByInvoiceNumber(invoiceNumber).orElseThrow(() -> new RuntimeException("Bill not found"));
        Map<String, Object> map = new HashMap<>();
        map.put("invoiceNumber", bill.getInvoiceNumber());
        map.put("customerName", bill.getCustomerName());
        map.put("finalTotal", bill.getFinalTotal());
        map.put("subtotal", bill.getSubtotal());
        map.put("charges", bill.getCharges());
        map.put("discount", bill.getDiscount());
        map.put("createdAt", bill.getCreatedAt());
        map.put("items", bill.getItems());
        return ResponseEntity.ok(map);
    }

    @PostMapping("/bills/archive")
    public ResponseEntity<?> archiveBills(@RequestBody List<Long> billIds, Authentication auth) {
        for (Long id : billIds) {
            billRepository.findById(id).ifPresent(bill -> {
                bill.setArchived(true);
                bill.setArchivedAt(LocalDateTime.now());
                bill.setArchivedBy(auth.getName());
                billRepository.save(bill);
                auditLogRepository.save(new com.freshcart.model.AuditLog("ARCHIVE_BILL", auth.getName(), null, id.toString(), "Bill archived"));
            });
        }
        return ResponseEntity.ok(Map.of("message", "Bills archived successfully"));
    }

    @PostMapping("/bills/restore")
    public ResponseEntity<?> restoreBills(@RequestBody List<Long> billIds, Authentication auth) {
        for (Long id : billIds) {
            billRepository.findById(id).ifPresent(bill -> {
                bill.setArchived(false);
                bill.setArchivedAt(null);
                bill.setArchivedBy(null);
                billRepository.save(bill);
                auditLogRepository.save(new com.freshcart.model.AuditLog("RESTORE_BILL", auth.getName(), null, id.toString(), "Bill restored"));
            });
        }
        return ResponseEntity.ok(Map.of("message", "Bills restored successfully"));
    }

    @PostMapping("/bills/export")
    public ResponseEntity<?> exportBills(@RequestBody List<Long> billIds) {
        List<Map<String, Object>> export = billIds.stream().map(id -> {
            Bill bill = billRepository.findById(id).orElse(null);
            if (bill == null) return null;
            Map<String, Object> map = new HashMap<>();
            map.put("invoiceNumber", bill.getInvoiceNumber());
            map.put("customerName", bill.getCustomerName());
            map.put("finalTotal", bill.getFinalTotal());
            map.put("createdAt", bill.getCreatedAt());
            map.put("items", bill.getItems());
            return map;
        }).filter(m -> m != null).collect(Collectors.toList());
        return ResponseEntity.ok(Map.of("exported", export, "message", "Export prepared"));
    }

    @DeleteMapping("/bills/permanent")
    public ResponseEntity<?> permanentDelete(@RequestBody Map<String, Object> payload, Authentication auth) {
        @SuppressWarnings("unchecked")
        List<Long> billIds = (List<Long>) payload.get("billIds");
        String confirmation = (String) payload.get("confirmation");
        if (!"PERMANENT_DELETE".equals(confirmation)) {
            return ResponseEntity.badRequest().body(Map.of("error", "Additional confirmation required. Type PERMANENT_DELETE to confirm."));
        }
        for (Long id : billIds) {
            billRepository.findById(id).ifPresent(bill -> {
                if (!Boolean.TRUE.equals(bill.getArchived())) {
                    throw new RuntimeException("Only archived bills can be permanently deleted");
                }
                auditLogRepository.save(new com.freshcart.model.AuditLog("PERMANENT_DELETE_BILL", auth.getName(), null, id.toString(), "Bill permanently deleted"));
                billRepository.delete(bill);
            });
        }
        return ResponseEntity.ok(Map.of("message", "Bills permanently deleted"));
    }

    @GetMapping("/audit-logs")
    public ResponseEntity<?> getAuditLogs() {
        return ResponseEntity.ok(auditLogRepository.findAllByOrderByPerformedAtDesc());
    }
}
