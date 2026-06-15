package com.example.auth.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Sample protected endpoints demonstrating role-based access control.
 */
@RestController
@RequestMapping("/api")
public class ResourceController {

    /** Any authenticated user */
    @GetMapping("/user/profile")
    @PreAuthorize("hasAnyRole('USER', 'MODERATOR', 'ADMIN')")
    public ResponseEntity<Map<String, Object>> userProfile(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(Map.of(
                "username", userDetails.getUsername(),
                "authorities", userDetails.getAuthorities().toString(),
                "message", "Welcome to your profile!"
        ));
    }

    /** Moderators and admins only */
    @GetMapping("/moderator/dashboard")
    @PreAuthorize("hasAnyRole('MODERATOR', 'ADMIN')")
    public ResponseEntity<Map<String, String>> moderatorDashboard() {
        return ResponseEntity.ok(Map.of("message", "Moderator dashboard — access granted"));
    }

    /** Admins only */
    @GetMapping("/admin/dashboard")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> adminDashboard() {
        return ResponseEntity.ok(Map.of("message", "Admin dashboard — access granted"));
    }

    /** Admins only — example of method-level security with expression */
    @DeleteMapping("/admin/users/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> deleteUser(@PathVariable Long id) {
        // In production: call userService.deleteUser(id)
        return ResponseEntity.ok(Map.of("message", "User %d deleted (demo)".formatted(id)));
    }
}
