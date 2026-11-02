package com.freshcart.controller;

import com.freshcart.model.AccountStatus;
import com.freshcart.model.User;
import com.freshcart.repository.AuditLogRepository;
import com.freshcart.repository.UserRepository;
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
@RequestMapping("/api/admin/requests")
@PreAuthorize("hasRole('ADMIN')")
public class AdminRequestsController {
    private final UserRepository userRepository;
    private final AuditLogRepository auditLogRepository;

    public AdminRequestsController(UserRepository userRepository, AuditLogRepository auditLogRepository) {
        this.userRepository = userRepository;
        this.auditLogRepository = auditLogRepository;
    }

    @GetMapping
    public ResponseEntity<?> getAllRequests() {
        List<Map<String, Object>> requests = userRepository.findAll().stream()
                .filter(u -> u.getRole().name().equals("USER"))
                .map(u -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", u.getId());
                    map.put("userId", u.getId());
                    map.put("username", u.getUsername());
                    map.put("fullName", u.getFullName());
                    map.put("email", u.getEmail());
                    map.put("status", u.getAccountStatus().name());
                    map.put("requestStatus", u.getAccountStatus().name());
                    map.put("accountStatus", u.getAccountStatus().name());
                    map.put("createdAt", u.getCreatedAt());
                    map.put("requestedAt", u.getCreatedAt());
                    return map;
                }).collect(Collectors.toList());
        return ResponseEntity.ok(requests);
    }

    @GetMapping("/pending")
    public ResponseEntity<?> getPendingRequests() {
        List<Map<String, Object>> requests = userRepository.findAll().stream()
                .filter(u -> u.getAccountStatus() == AccountStatus.PENDING)
                .map(u -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", u.getId());
                    map.put("username", u.getUsername());
                    map.put("fullName", u.getFullName());
                    map.put("email", u.getEmail());
                    map.put("status", u.getAccountStatus().name());
                    map.put("createdAt", u.getCreatedAt());
                    return map;
                }).collect(Collectors.toList());
        return ResponseEntity.ok(requests);
    }

    @PutMapping("/{id}/approve")
    public ResponseEntity<?> approveRequest(@PathVariable Long id, Authentication auth) {
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        user.setAccountStatus(AccountStatus.ACTIVE);
        user.setApprovedBy(auth.getName());
        user.setApprovalTimestamp(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
        try {
            auditLogRepository.save(new com.freshcart.model.AuditLog("APPROVE_USER", auth.getName(), user.getUsername(), id.toString(), "User approved"));
        } catch (Exception ignored) {}
        return ResponseEntity.ok(Map.of("message", "User approved successfully"));
    }

    @PutMapping("/{id}/reject")
    public ResponseEntity<?> rejectRequest(@PathVariable Long id, Authentication auth) {
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        user.setAccountStatus(AccountStatus.REJECTED);
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
        try {
            auditLogRepository.save(new com.freshcart.model.AuditLog("REJECT_USER", auth.getName(), user.getUsername(), id.toString(), "User rejected"));
        } catch (Exception ignored) {}
        return ResponseEntity.ok(Map.of("message", "Request rejected"));
    }
}
