package com.recipeadjuster.userprofile.service;

import com.recipeadjuster.userprofile.model.entity.RefreshToken;
import com.recipeadjuster.userprofile.model.entity.User;
import com.recipeadjuster.userprofile.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
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
@Slf4j
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final SecureRandom secureRandom = new SecureRandom();

    @Transactional
    public String createRefreshToken(User user) {
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

    @Transactional
    public RefreshToken rotateRefreshToken(String oldToken) {
        String tokenHash = hashToken(oldToken);
        RefreshToken token = refreshTokenRepository.findByTokenHash(tokenHash)
                .orElseThrow(() -> new IllegalArgumentException("Invalid refresh token"));

        if (token.getIsUsed()) {
            log.warn("Attempted reuse of refresh token for user: {}", token.getUser().getId());
            throw new IllegalArgumentException("Refresh token already used");
        }

        if (token.isExpired()) {
            throw new IllegalArgumentException("Refresh token expired");
        }

        token.setIsUsed(true);
        refreshTokenRepository.save(token);

        return token;
    }

    @Scheduled(cron = "0 0 3 * * *")
    @Transactional
    public void cleanupExpiredTokens() {
        Instant now = Instant.now();
        Instant threshold = now.minus(24, ChronoUnit.HOURS);
        
        int deleted = refreshTokenRepository.deleteExpiredAndUsedTokens(now, threshold);
        log.info("Cleaned up {} expired/used refresh tokens", deleted);
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
