
package com.freshcart.config;

import com.freshcart.model.AccountStatus;
import com.freshcart.model.Role;
import com.freshcart.model.User;
import com.freshcart.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component
public class AdminBootstrap implements ApplicationRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.bootstrap.admin.enabled:false}")
    private boolean enabled;

    @Value("${app.bootstrap.admin.username:}")
    private String username;

    @Value("${app.bootstrap.admin.password:}")
    private String password;

    public AdminBootstrap(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (!enabled) {
            return;
        }

        if (userRepository.count() > 0) {
            System.out.println("Admin bootstrap skipped: database already contains users.");
            return;
        }

        if (username == null || username.isBlank()
                || password == null || password.length() < 12) {
            throw new IllegalStateException(
                    "Admin bootstrap requires a username and a password of at least 12 characters.");
        }

        User admin = User.builder()
                .username(username.trim())
                .passwordHash(passwordEncoder.encode(password))
                .role(Role.ADMIN)
                .accountStatus(AccountStatus.ACTIVE)
                .approvedBy("SYSTEM")
                .approvalTimestamp(LocalDateTime.now())
                .build();

        userRepository.save(admin);
        System.out.println("Initial administrator account created.");
    }
}
