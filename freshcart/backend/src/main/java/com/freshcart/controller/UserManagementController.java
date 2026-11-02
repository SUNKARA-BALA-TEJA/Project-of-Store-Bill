package com.freshcart.controller;

import com.freshcart.model.AccountStatus;
import com.freshcart.model.Permission;
import com.freshcart.model.Role;
import com.freshcart.model.User;
import com.freshcart.repository.AuditLogRepository;
import com.freshcart.repository.UserPermissionRepository;
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
@RequestMapping("/api/admin/users")
@PreAuthorize("hasRole('ADMIN')")
public class UserManagementController {
    private final UserRepository userRepository;
    private final UserPermissionRepository userPermissionRepository;
    private final AuditLogRepository auditLogRepository;

    public UserManagementController(UserRepository userRepository, UserPermissionRepository userPermissionRepository, AuditLogRepository auditLogRepository) {
        this.userRepository = userRepository;
        this.userPermissionRepository = userPermissionRepository;
        this.auditLogRepository = auditLogRepository;
    }

    @GetMapping
    public ResponseEntity<?> getAllUsers() {
        List<Map<String, Object>> users = userRepository.findAll().stream().map(u -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", u.getId());
            map.put("username", u.getUsername());
            map.put("role", u.getRole().name());
            map.put("accountStatus", u.getAccountStatus().name());
            map.put("approvedBy", u.getApprovedBy());
            map.put("approvalTimestamp", u.getApprovalTimestamp());
            map.put("createdAt", u.getCreatedAt());
            List<String> perms = userPermissionRepository.findByUser(u).stream()
                    .map(p -> p.getPermission().name()).collect(Collectors.toList());
            map.put("permissions", perms);
            return map;
        }).collect(Collectors.toList());
        return ResponseEntity.ok(users);
    }

    @PostMapping("/{id}/approve")
    public ResponseEntity<?> approveUser(@PathVariable Long id, Authentication auth) {
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        if (user.getUsername().equals(auth.getName())) {
            return ResponseEntity.badRequest().body(Map.of("error", "Cannot approve your own account"));
        }
        user.setAccountStatus(AccountStatus.ACTIVE);
        user.setApprovedBy(auth.getName());
        user.setApprovalTimestamp(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
        auditLogRepository.save(new com.freshcart.model.AuditLog("APPROVE_USER", auth.getName(), user.getUsername(), id.toString(), "User approved"));
        return ResponseEntity.ok(Map.of("message", "User approved successfully"));
    }

    @PostMapping("/{id}/reject")
    public ResponseEntity<?> rejectUser(@PathVariable Long id, Authentication auth) {
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        if (user.getUsername().equals(auth.getName())) {
            return ResponseEntity.badRequest().body(Map.of("error", "Cannot reject your own account"));
        }
        user.setAccountStatus(AccountStatus.REJECTED);
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
        auditLogRepository.save(new com.freshcart.model.AuditLog("REJECT_USER", auth.getName(), user.getUsername(), id.toString(), "User rejected"));
        return ResponseEntity.ok(Map.of("message", "User rejected"));
    }

    @PostMapping("/{id}/activate")
    public ResponseEntity<?> activateUser(@PathVariable Long id, Authentication auth) {
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        user.setAccountStatus(AccountStatus.ACTIVE);
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
        auditLogRepository.save(new com.freshcart.model.AuditLog("ACTIVATE_USER", auth.getName(), user.getUsername(), id.toString(), "User activated"));
        return ResponseEntity.ok(Map.of("message", "User activated"));
    }

    @PostMapping("/{id}/deactivate")
    public ResponseEntity<?> deactivateUser(@PathVariable Long id, Authentication auth) {
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        if (user.getUsername().equals(auth.getName())) {
            return ResponseEntity.badRequest().body(Map.of("error", "Cannot deactivate your own account"));
        }
        user.setAccountStatus(AccountStatus.DISABLED);
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
        auditLogRepository.save(new com.freshcart.model.AuditLog("DEACTIVATE_USER", auth.getName(), user.getUsername(), id.toString(), "User deactivated"));
        return ResponseEntity.ok(Map.of("message", "User deactivated"));
    }

    @PostMapping("/{id}/role")
    public ResponseEntity<?> changeRole(@PathVariable Long id, @RequestParam String role, Authentication auth) {
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        if (user.getUsername().equals(auth.getName())) {
            return ResponseEntity.badRequest().body(Map.of("error", "Cannot change your own role"));
        }
        try {
            Role newRole = Role.valueOf(role.toUpperCase());
            if (newRole == Role.ADMIN && !auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
                return ResponseEntity.badRequest().body(Map.of("error", "Not authorized"));
            }
            user.setRole(newRole);
            user.setUpdatedAt(LocalDateTime.now());
            userRepository.save(user);
            auditLogRepository.save(new com.freshcart.model.AuditLog("CHANGE_ROLE", auth.getName(), user.getUsername(), id.toString(), "Role changed to " + newRole));
            return ResponseEntity.ok(Map.of("message", "Role updated"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid role"));
        }
    }

    @PostMapping("/{id}/permissions")
    public ResponseEntity<?> updatePermissions(@PathVariable Long id, @RequestBody List<String> permissions, Authentication auth) {
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        userPermissionRepository.deleteByUser(user);
        for (String p : permissions) {
            try {
                Permission perm = Permission.valueOf(p);
                userPermissionRepository.save(new com.freshcart.model.UserPermission(user, perm, auth.getName()));
            } catch (Exception ignored) {}
        }
        auditLogRepository.save(new com.freshcart.model.AuditLog("UPDATE_PERMISSIONS", auth.getName(), user.getUsername(), id.toString(), "Permissions updated"));
        return ResponseEntity.ok(Map.of("message", "Permissions updated"));
    }
}
