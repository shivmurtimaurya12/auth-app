package com.example.auth.config;

import com.example.auth.entity.Role;
import com.example.auth.entity.User;
import com.example.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * Seeds default users on first startup.
 * Remove or secure this in production.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        seedUser("admin",   "admin@example.com",   "Admin1234!",   Set.of(Role.ROLE_ADMIN, Role.ROLE_USER));
        seedUser("mod",     "mod@example.com",     "Mod12345!",    Set.of(Role.ROLE_MODERATOR, Role.ROLE_USER));
        seedUser("user",    "user@example.com",    "User1234!",    Set.of(Role.ROLE_USER));
    }

    private void seedUser(String username, String email, String rawPassword, Set<Role> roles) {
        if (!userRepository.existsByUsername(username)) {
            User user = User.builder()
                    .username(username)
                    .email(email)
                    .password(passwordEncoder.encode(rawPassword))
                    .roles(roles)
                    .build();
            userRepository.save(user);
            log.info("Seeded user: {} with roles {}", username, roles);
        }
    }
}
