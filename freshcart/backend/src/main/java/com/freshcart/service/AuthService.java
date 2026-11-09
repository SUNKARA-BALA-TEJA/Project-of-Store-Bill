package com.freshcart.service;

import com.freshcart.dto.AuthDto;
import com.freshcart.model.AccountStatus;
import com.freshcart.model.Role;
import com.freshcart.model.User;
import com.freshcart.repository.UserRepository;
import com.freshcart.security.JwtUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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
            throw new AuthException("VALIDATION_ERROR", "All fields are required");
        }
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new AuthException("VALIDATION_ERROR", "Passwords do not match");
        }

        String username = request.getUsername().trim();
        String email = request.getEmail().trim();

        if (userRepository.existsByUsername(username)) {
            throw new AuthException("USERNAME_EXISTS", "Username already exists");
        }
        if (userRepository.existsByEmail(email)) {
            throw new AuthException("EMAIL_EXISTS", "Email address is already registered");
        }

        User user = User.builder()
                .fullName(request.getFullName().trim())
                .username(username)
                .email(email)
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
        String username = request.getUsername() == null ? "" : request.getUsername().trim();
        String rawPassword = request.getPassword();

        if (username.isEmpty() || rawPassword == null || rawPassword.isEmpty()) {
            throw new AuthException("VALIDATION_ERROR", "Username and password are required");
        }

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new AuthException("INVALID_CREDENTIALS", "Invalid username or password"));

        // Verify the password with the same encoder used by registration.
        // Do this before exposing account-status information.
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, rawPassword)
            );
        } catch (BadCredentialsException | UsernameNotFoundException e) {
            throw new AuthException("INVALID_CREDENTIALS", "Invalid username or password");
        } catch (DisabledException | LockedException e) {
            throw new AuthException("ACCOUNT_DISABLED", "Your account is disabled. Please contact the administrator.");
        } catch (AuthenticationException e) {
            throw new AuthException("INVALID_CREDENTIALS", "Invalid username or password");
        }

        switch (user.getAccountStatus()) {
            case ACTIVE -> {
                // Account may sign in.
            }
            case PENDING -> throw new AuthException(
                    "ACCOUNT_PENDING",
                    "Your registration is awaiting administrator approval."
            );
            case REJECTED -> throw new AuthException(
                    "ACCOUNT_REJECTED",
                    "Your registration request was rejected. Please contact the administrator."
            );
            case DISABLED, INACTIVE -> throw new AuthException(
                    "ACCOUNT_DISABLED",
                    "Your account is disabled. Please contact the administrator."
            );
            default -> throw new AuthException(
                    "ACCOUNT_INACTIVE",
                    "Your account is not active. Please contact the administrator."
            );
        }

        String token = jwtUtil.generateToken(user.getUsername());
        AuthDto.AuthResponse response = new AuthDto.AuthResponse();
        response.setToken(token);
        response.setUsername(user.getUsername());
        response.setRole(user.getRole().name());
        return response;
    }

   }
