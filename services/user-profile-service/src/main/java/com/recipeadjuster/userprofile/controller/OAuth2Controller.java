package com.recipeadjuster.userprofile.controller;

import com.recipeadjuster.userprofile.model.dto.LoginResponse;
import com.recipeadjuster.userprofile.model.entity.User;
import com.recipeadjuster.userprofile.repository.UserRepository;
import com.recipeadjuster.userprofile.security.JwtService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

@RestController
@RequestMapping("/api/v1/auth/oauth2")
@RequiredArgsConstructor
public class OAuth2Controller {

    private final UserRepository userRepository;
    private final JwtService jwtService;

    @GetMapping("/success")
    public RedirectView oauth2Success(
            @AuthenticationPrincipal OAuth2User oauth2User,
            HttpServletResponse response) {
        
        String email = oauth2User.getAttribute("email");
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalStateException("User not found after OAuth2 login"));

        String accessToken = jwtService.generateAccessToken(user);
        
        Cookie jwtCookie = new Cookie("accessToken", accessToken);
        jwtCookie.setHttpOnly(false);
        jwtCookie.setSecure(true);
        jwtCookie.setPath("/");
        jwtCookie.setMaxAge(3600);
        response.addCookie(jwtCookie);

        return new RedirectView("http://localhost:4200/auth/callback?token=" + accessToken);
    }

    @GetMapping("/failure")
    public RedirectView oauth2Failure() {
        return new RedirectView("http://localhost:4200/auth/error");
    }
}
