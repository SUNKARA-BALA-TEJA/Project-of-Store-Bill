package com.freshcart.controller;

import com.freshcart.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/setup")
public class SetupController {
    private final AuthService authService;

    public SetupController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/admin")
    public ResponseEntity<?> createAdmin(@RequestParam String username, @RequestParam String password) {
        try {
            authService.createAdmin(username, password);
            return ResponseEntity.ok(Map.of("message", "Admin created or already exists"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
