package com.example.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Set;

// ── Request DTOs ──────────────────────────────────────────────────────────────

public class AuthDtos {

    @Data
    public static class RegisterRequest {
        @NotBlank @Size(min = 3, max = 50)
        private String username;

        @NotBlank @Email @Size(max = 100)
        private String email;

        @NotBlank @Size(min = 8, max = 40)
        private String password;

        /** Optional roles; defaults to ROLE_USER if omitted */
        private Set<String> roles;
    }

    @Data
    public static class LoginRequest {
        @NotBlank
        private String username;

        @NotBlank
        private String password;
    }

    @Data
    public static class RefreshTokenRequest {
        @NotBlank
        private String refreshToken;
    }

    // ── Response DTOs ─────────────────────────────────────────────────────────

    @Data
    public static class JwtResponse {
        private final String accessToken;
        private final String refreshToken;
        private final String tokenType = "Bearer";
        private final Long id;
        private final String username;
        private final String email;
        private final Set<String> roles;
    }

    @Data
    public static class TokenRefreshResponse {
        private final String accessToken;
        private final String refreshToken;
        private final String tokenType = "Bearer";
    }

    @Data
    public static class MessageResponse {
        private final String message;
    }
}
