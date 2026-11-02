package com.freshcart.controller;

import com.freshcart.model.User;
import com.freshcart.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserProfileController {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserProfileController(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));
        }
        String username = authentication.getName();
        User user = userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found"));
        Map<String, Object> profile = new HashMap<>();
        profile.put("id", user.getId());
        profile.put("userId", user.getId());
        profile.put("fullName", user.getFullName());
        profile.put("username", user.getUsername());
        profile.put("email", user.getEmail());
        profile.put("role", user.getRole().name());
        profile.put("accountRole", user.getRole().name());
        profile.put("accountStatus", user.getAccountStatus().name());
        profile.put("status", user.getAccountStatus().name());
        profile.put("registrationDate", user.getCreatedAt());
        profile.put("createdAt", user.getCreatedAt());
        profile.put("lastLoginDate", null);
        return ResponseEntity.ok(profile);
    }

    @PutMapping("/profile")
    public ResponseEntity<?> updateProfile(@RequestBody Map<String, Object> payload, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));
        }
        String username = authentication.getName();
        User user = userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found"));
        if (payload.containsKey("fullName")) {
            user.setFullName((String) payload.get("fullName"));
        }
        if (payload.containsKey("email")) {
            String email = (String) payload.get("email");
            if (email != null && !email.isEmpty()) {
                // Check uniqueness
                userRepository.findAll().stream()
                    .filter(u -> u.getEmail() != null && u.getEmail().equalsIgnoreCase(email) && !u.getId().equals(user.getId()))
                    .findFirst()
                    .ifPresent(u -> { throw new RuntimeException("Email already exists"); });
                user.setEmail(email);
            }
        }
        user.setUpdatedAt(java.time.LocalDateTime.now());
        userRepository.save(user);
        return ResponseEntity.ok(Map.of("message", "Profile updated successfully"));
    }

    @PutMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody Map<String, String> payload, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));
        }
        String username = authentication.getName();
        User user = userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found"));
        String currentPassword = payload.get("currentPassword");
        String newPassword = payload.get("newPassword");
        String confirmPassword = payload.get("confirmPassword");
        if (currentPassword == null || newPassword == null || confirmPassword == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "All fields are required"));
        }
        if (!passwordEncoder.matches(currentPassword, user.getPasswordHash())) {
            return ResponseEntity.badRequest().body(Map.of("error", "Incorrect current password"));
        }
        if (!newPassword.equals(confirmPassword)) {
            return ResponseEntity.badRequest().body(Map.of("error", "New passwords do not match"));
        }
        if (newPassword.equals(currentPassword)) {
            return ResponseEntity.badRequest().body(Map.of("error", "New password must be different from current password"));
        }
        if (newPassword.length() < 4) {
            return ResponseEntity.badRequest().body(Map.of("error", "Password must meet strength requirements"));
        }
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        user.setUpdatedAt(java.time.LocalDateTime.now());
        userRepository.save(user);
        return ResponseEntity.ok(Map.of("message", "Password changed successfully"));
    }
}
