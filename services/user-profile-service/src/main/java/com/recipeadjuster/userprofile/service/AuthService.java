package com.recipeadjuster.userprofile.service;

import com.recipeadjuster.userprofile.model.dto.LoginRequest;
import com.recipeadjuster.userprofile.model.dto.LoginResponse;
import com.recipeadjuster.userprofile.model.dto.RegisterRequest;
import com.recipeadjuster.userprofile.model.dto.UserResponse;
import com.recipeadjuster.userprofile.model.entity.RefreshToken;
import com.recipeadjuster.userprofile.model.entity.User;
import com.recipeadjuster.userprofile.model.entity.UserPreferences;
import com.recipeadjuster.userprofile.repository.RefreshTokenRepository;
import com.recipeadjuster.userprofile.repository.UserPreferencesRepository;
import com.recipeadjuster.userprofile.repository.UserRepository;
import com.recipeadjuster.userprofile.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final UserPreferencesRepository userPreferencesRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final SecureRandom secureRandom = new SecureRandom();

    @Transactional
    public UserResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already registered");
        }

        User user = User.builder()
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .displayName(request.getDisplayName())
                .isGuest(false)
                .authProvider(User.AuthProvider.LOCAL)
                .build();

        user = userRepository.save(user);

        UserPreferences preferences = UserPreferences.builder()
                .user(user)
                .build();
        userPreferencesRepository.save(preferences);

        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .displayName(user.getDisplayName())
                .avatarUrl(user.getAvatarUrl())
                .createdAt(user.getCreatedAt())
                .build();
    }

    @Transactional
    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new IllegalArgumentException("Invalid email or password");
        }

        user.setLastLoginAt(Instant.now());
        userRepository.save(user);

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = generateRefreshToken(user);

        return LoginResponse.builder()
                .accessToken(accessToken)
                .tokenType("Bearer")
                .expiresIn(3600)
                .build();
    }

    private String generateRefreshToken(User user) {
        byte[] tokenBytes = new byte[64];
        secureRandom.nextBytes(tokenBytes);
        String token = Base64.getUrlEncoder().withoutPadding().encodeToString(tokenBytes);

        RefreshToken refreshToken = RefreshToken.builder()
                .tokenHash(hashToken(token))
                .user(user)
                .expiresAt(Instant.now().plus(7, ChronoUnit.DAYS))
                .build();

        refreshTokenRepository.save(refreshToken);
        return token;
    }

    private String hashToken(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(token.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Failed to hash token", e);
        }
    }
}
