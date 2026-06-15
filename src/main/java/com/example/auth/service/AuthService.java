package com.example.auth.service;

import com.example.auth.dto.AuthDtos.*;
import com.example.auth.entity.RefreshToken;
import com.example.auth.entity.Role;
import com.example.auth.entity.User;
import com.example.auth.repository.UserRepository;
import com.example.auth.security.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final RefreshTokenService refreshTokenService;

    // ── Register ──────────────────────────────────────────────────────────────

    @Transactional
    public MessageResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username '%s' is already taken"
                    .formatted(request.getUsername()));
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email '%s' is already in use"
                    .formatted(request.getEmail()));
        }

        Set<Role> roles = resolveRoles(request.getRoles());

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .roles(roles)
                .build();

        userRepository.save(user);
        return new MessageResponse("User registered successfully");
    }

    // ── Login ─────────────────────────────────────────────────────────────────

    @Transactional
    public JwtResponse login(LoginRequest request) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(), request.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(auth);

        var userDetails = (org.springframework.security.core.userdetails.User) auth.getPrincipal();

        String accessToken = jwtUtils.generateToken(userDetails);

        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow();

        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.getId());

        Set<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());

        return new JwtResponse(
                accessToken,
                refreshToken.getToken(),
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                roles
        );
    }

    // ── Refresh token ─────────────────────────────────────────────────────────

    public TokenRefreshResponse refreshToken(RefreshTokenRequest request) {
        RefreshToken token = refreshTokenService.findByToken(request.getRefreshToken());
        refreshTokenService.verifyExpiration(token);

        User user = token.getUser();

        // Build minimal UserDetails for token generation
        var userDetails = org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .authorities(user.getRoles().stream()
                        .map(r -> new org.springframework.security.core.authority.SimpleGrantedAuthority(r.name()))
                        .toList())
                .build();

        String newAccessToken = jwtUtils.generateToken(userDetails);
        return new TokenRefreshResponse(newAccessToken, token.getToken());
    }

    // ── Logout ────────────────────────────────────────────────────────────────

    @Transactional
    public MessageResponse logout(Long userId) {
        refreshTokenService.deleteByUserId(userId);
        SecurityContextHolder.clearContext();
        return new MessageResponse("Logged out successfully");
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private Set<Role> resolveRoles(Set<String> requestedRoles) {
        if (requestedRoles == null || requestedRoles.isEmpty()) {
            return Set.of(Role.ROLE_USER);
        }
        return requestedRoles.stream()
                .map(r -> switch (r.toLowerCase()) {
                    case "admin" -> Role.ROLE_ADMIN;
                    case "moderator", "mod" -> Role.ROLE_MODERATOR;
                    default -> Role.ROLE_USER;
                })
                .collect(Collectors.toSet());
    }
}
