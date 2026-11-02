package com.freshcart.controller;

import com.freshcart.dto.AuthDto;
import com.freshcart.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody AuthDto.RegisterRequest request) {
        try {
            AuthDto.AuthResponse response = authService.register(request);
            return ResponseEntity.ok(Map.of(
                    "message", response.getMessage() != null ? response.getMessage() : "Registration successful. Your account is pending administrator approval.",
                    "username", response.getUsername(),
                    "status", "PENDING"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody AuthDto.LoginRequest request) {
        try {
            return ResponseEntity.ok(authService.login(request));
        } catch (Exception e) {
            String msg = e.getMessage();
            if (msg != null && msg.contains("pending approval")) {
                return ResponseEntity.badRequest().body(Map.of("error", "Your registration is awaiting administrator approval."));
            }
            if (msg != null && msg.contains("inactive") && msg.contains("rejected")) {
                return ResponseEntity.badRequest().body(Map.of("error", "Your registration request was rejected. Please contact the administrator."));
            }
            if (msg != null && (msg.contains("rejected") || msg.contains("REJECTED"))) {
                return ResponseEntity.badRequest().body(Map.of("error", "Your registration request was rejected. Please contact the administrator."));
            }
            return ResponseEntity.badRequest().body(Map.of("error", msg != null ? msg : "Invalid credentials"));
        }
    }
}
