package com.recipeadjuster.userprofile.controller;

import com.recipeadjuster.userprofile.model.dto.LoginRequest;
import com.recipeadjuster.userprofile.model.dto.LoginResponse;
import com.recipeadjuster.userprofile.model.dto.RegisterRequest;
import com.recipeadjuster.userprofile.model.dto.UserResponse;
import com.recipeadjuster.userprofile.model.entity.User;
import com.recipeadjuster.userprofile.repository.UserRepository;
import com.recipeadjuster.userprofile.security.JwtService;
import com.recipeadjuster.userprofile.service.AuthService;
import com.recipeadjuster.userprofile.service.RefreshTokenService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final RefreshTokenService refreshTokenService;
    private final JwtService jwtService;
    private final UserRepository userRepository;

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody RegisterRequest request) {
        UserResponse response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletResponse httpResponse) {
        LoginResponse response = authService.login(request);

        Cookie refreshTokenCookie = new Cookie("refreshToken", response.getAccessToken());
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setSecure(true);
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setMaxAge(7 * 24 * 60 * 60);
        refreshTokenCookie.setAttribute("SameSite", "Strict");
        httpResponse.addCookie(refreshTokenCookie);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/guest")
    public ResponseEntity<LoginResponse> createGuestUser(HttpServletResponse httpResponse) {
        User guestUser = User.builder()
                .email("guest-" + UUID.randomUUID() + "@temp.local")
                .passwordHash("")
                .displayName("Guest User")
                .isGuest(true)
                .authProvider(User.AuthProvider.LOCAL)
                .build();

        guestUser = userRepository.save(guestUser);

        String accessToken = jwtService.generateAccessToken(guestUser);
        String refreshToken = refreshTokenService.createRefreshToken(guestUser);

        Cookie refreshTokenCookie = new Cookie("refreshToken", refreshToken);
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setSecure(true);
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setMaxAge(7 * 24 * 60 * 60);
        refreshTokenCookie.setAttribute("SameSite", "Strict");
        httpResponse.addCookie(refreshTokenCookie);

        return ResponseEntity.ok(LoginResponse.builder()
                .accessToken(accessToken)
                .tokenType("Bearer")
                .expiresIn(3600)
                .build());
    }

    @PostMapping("/refresh")
    public ResponseEntity<LoginResponse> refreshToken(
            HttpServletRequest request,
            HttpServletResponse httpResponse) {
        
        String refreshToken = Arrays.stream(request.getCookies())
                .filter(cookie -> "refreshToken".equals(cookie.getName()))
                .findFirst()
                .map(Cookie::getValue)
                .orElseThrow(() -> new IllegalArgumentException("Refresh token not found"));

        var tokenEntity = refreshTokenService.rotateRefreshToken(refreshToken);
        User user = tokenEntity.getUser();

        String newAccessToken = jwtService.generateAccessToken(user);
        String newRefreshToken = refreshTokenService.createRefreshToken(user);

        Cookie newRefreshTokenCookie = new Cookie("refreshToken", newRefreshToken);
        newRefreshTokenCookie.setHttpOnly(true);
        newRefreshTokenCookie.setSecure(true);
        newRefreshTokenCookie.setPath("/");
        newRefreshTokenCookie.setMaxAge(7 * 24 * 60 * 60);
        newRefreshTokenCookie.setAttribute("SameSite", "Strict");
        httpResponse.addCookie(newRefreshTokenCookie);

        return ResponseEntity.ok(LoginResponse.builder()
                .accessToken(newAccessToken)
                .tokenType("Bearer")
                .expiresIn(3600)
                .build());
    }
}
