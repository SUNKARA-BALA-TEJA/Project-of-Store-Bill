package com.freshcart.service;

import com.freshcart.dto.AuthDto;
import com.freshcart.model.AccountStatus;
import com.freshcart.model.Role;
import com.freshcart.model.User;
import com.freshcart.repository.UserRepository;
import com.freshcart.security.JwtUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, 
                      AuthenticationManager authenticationManager, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }

    public AuthDto.AuthResponse register(AuthDto.RegisterRequest request) {
        if (request.getFullName() == null || request.getFullName().trim().isEmpty() ||
            request.getUsername() == null || request.getUsername().trim().isEmpty() ||
            request.getEmail() == null || request.getEmail().trim().isEmpty() ||
            request.getPassword() == null || request.getConfirmPassword() == null) {
            throw new RuntimeException("All fields are required");
        }
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new RuntimeException("Passwords do not match");
        }
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username already exists");
        }
        User user = User.builder()
                .fullName(request.getFullName())
                .username(request.getUsername())
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .accountStatus(AccountStatus.PENDING)
                .build();
        userRepository.save(user);
        AuthDto.AuthResponse response = new AuthDto.AuthResponse();
        response.setToken(null);
        response.setUsername(user.getUsername());
        response.setRole(user.getRole().name());
        response.setMessage("Registration submitted successfully. Please wait for administrator approval.");
        return response;
    }

    public AuthDto.AuthResponse login(AuthDto.LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));
        if (user.getAccountStatus() == AccountStatus.PENDING) {
            throw new RuntimeException("Account pending approval or inactive. Please contact administrator.");
        }
        if (user.getAccountStatus() == AccountStatus.REJECTED || user.getAccountStatus() == AccountStatus.DISABLED || user.getAccountStatus() == AccountStatus.INACTIVE) {
            throw new RuntimeException("Your registration request was rejected. Please contact the administrator.");
        }
        if (user.getAccountStatus() != AccountStatus.ACTIVE) {
            throw new RuntimeException("Account pending approval or inactive. Please contact administrator.");
        }
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );
        } catch (Exception e) {
            throw new RuntimeException("Invalid credentials");
        }
        String token = jwtUtil.generateToken(user.getUsername());
        AuthDto.AuthResponse response = new AuthDto.AuthResponse();
        response.setToken(token);
        response.setUsername(user.getUsername());
        response.setRole(user.getRole().name());
        return response;
    }

    public void createAdmin(String username, String password) {
        if (userRepository.findByUsername(username).isPresent()) {
            User existing = userRepository.findByUsername(username).get();
            existing.setRole(Role.ADMIN);
            existing.setAccountStatus(AccountStatus.ACTIVE);
            existing.setApprovedBy("SYSTEM");
            existing.setApprovalTimestamp(java.time.LocalDateTime.now());
            userRepository.save(existing);
            return;
        }
        if (userRepository.count() == 0) {
            User admin = User.builder()
                    .username(username)
                    .passwordHash(passwordEncoder.encode(password))
                    .role(Role.ADMIN)
                    .accountStatus(AccountStatus.ACTIVE)
                    .approvedBy("SYSTEM")
                    .approvalTimestamp(java.time.LocalDateTime.now())
                    .build();
            userRepository.save(admin);
        }
    }
}
