package com.recipeadjuster.userprofile.service;

import com.recipeadjuster.userprofile.model.entity.User;
import com.recipeadjuster.userprofile.model.entity.UserPreferences;
import com.recipeadjuster.userprofile.repository.UserPreferencesRepository;
import com.recipeadjuster.userprofile.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class OAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;
    private final UserPreferencesRepository userPreferencesRepository;

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oauth2User = super.loadUser(userRequest);
        
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        String email = oauth2User.getAttribute("email");
        String name = oauth2User.getAttribute("name");
        
        if (email == null) {
            throw new OAuth2AuthenticationException("Email not found from OAuth2 provider");
        }

        User.AuthProvider provider = User.AuthProvider.valueOf(registrationId.toUpperCase());
        
        Optional<User> existingUser = userRepository.findByEmail(email);
        
        if (existingUser.isPresent()) {
            User user = existingUser.get();
            user.setLastLoginAt(Instant.now());
            
            if (user.getAuthProvider() != provider) {
                user.setAuthProvider(provider);
            }
            
            userRepository.save(user);
            log.info("Existing user logged in via OAuth2: {}", email);
        } else {
            User newUser = User.builder()
                    .email(email)
                    .displayName(name)
                    .passwordHash("")
                    .isGuest(false)
                    .authProvider(provider)
                    .build();
            
            newUser = userRepository.save(newUser);
            
            UserPreferences preferences = UserPreferences.builder()
                    .user(newUser)
                    .build();
            
            userPreferencesRepository.save(preferences);
            log.info("New user created via OAuth2: {}", email);
        }
        
        return oauth2User;
    }
}
