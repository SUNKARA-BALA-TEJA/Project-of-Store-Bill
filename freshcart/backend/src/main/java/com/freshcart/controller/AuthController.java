package com.freshcart.controller;

import com.freshcart.dto.AuthDto;
import com.freshcart.service.AuthException;
import com.freshcart.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
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
        } catch (AuthException e) {
            return ResponseEntity.badRequest().body(errorBody(e.getCode(), e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(errorBody("REGISTRATION_ERROR", "Registration failed. Please try again."));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody AuthDto.LoginRequest request) {
        try {
            return ResponseEntity.ok(authService.login(request));
        } catch (AuthException e) {
            HttpStatus status = switch (e.getCode()) {
                case "VALIDATION_ERROR" -> HttpStatus.BAD_REQUEST;
                case "INVALID_CREDENTIALS" -> HttpStatus.UNAUTHORIZED;
                case "ACCOUNT_PENDING", "ACCOUNT_REJECTED", "ACCOUNT_DISABLED", "ACCOUNT_INACTIVE" -> HttpStatus.FORBIDDEN;
                default -> HttpStatus.BAD_REQUEST;
            };
            return ResponseEntity.status(status).body(errorBody(e.getCode(), e.getMessage()));
        } catch (Exception e) {
            // Do not turn server failures into a misleading "Invalid credentials" message.
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(errorBody("AUTHENTICATION_ERROR", "The authentication service is temporarily unavailable. Please try again."));
        }
    }

    private Map<String, String> errorBody(String code, String message) {
        Map<String, String> body = new LinkedHashMap<>();
        body.put("error", message);
        body.put("code", code);
        return body;
    }
}
