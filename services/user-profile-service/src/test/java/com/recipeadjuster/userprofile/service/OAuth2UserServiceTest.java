package com.recipeadjuster.userprofile.service;

import com.recipeadjuster.userprofile.model.entity.User;
import com.recipeadjuster.userprofile.model.entity.UserPreferences;
import com.recipeadjuster.userprofile.repository.UserPreferencesRepository;
import com.recipeadjuster.userprofile.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OAuth2UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserPreferencesRepository userPreferencesRepository;

    private OAuth2UserService oauth2UserService;

    private OAuth2UserRequest userRequest;
    private OAuth2User oauth2User;

    @BeforeEach
    void setUp() {
        oauth2UserService = spy(new OAuth2UserService(userRepository, userPreferencesRepository));
        
        ClientRegistration clientRegistration = ClientRegistration.withRegistrationId("google")
                .clientId("test-client-id")
                .clientSecret("test-client-secret")
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .redirectUri("http://localhost:8084/login/oauth2/code/google")
                .authorizationUri("https://accounts.google.com/o/oauth2/auth")
                .tokenUri("https://oauth2.googleapis.com/token")
                .userInfoUri("https://www.googleapis.com/oauth2/v3/userinfo")
                .userNameAttributeName("sub")
                .build();

        OAuth2AccessToken accessToken = new OAuth2AccessToken(
                OAuth2AccessToken.TokenType.BEARER,
                "test-token",
                null,
                null
        );

        userRequest = new OAuth2UserRequest(clientRegistration, accessToken);

        oauth2User = new DefaultOAuth2User(
                null,
                Map.of(
                        "sub", "google-user-123",
                        "email", "test@gmail.com",
                        "name", "Test User"
                ),
                "sub"
        );
    }

    @Test
    @Disabled("Requires OAuth2 integration test setup with mock server")
    void shouldCreateNewUserOnFirstOAuth2Login() {
        // Mock parent class to return oauth2User
        doReturn(oauth2User).when(oauth2UserService).loadUser(userRequest);
        doCallRealMethod().when(oauth2UserService).loadUser(userRequest);
        
        when(userRepository.findByEmail("test@gmail.com")).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId(UUID.randomUUID());
            assertThat(user.getEmail()).isEqualTo("test@gmail.com");
            assertThat(user.getDisplayName()).isEqualTo("Test User");
            assertThat(user.getAuthProvider()).isEqualTo(User.AuthProvider.GOOGLE);
            return user;
        });

        OAuth2User result = oauth2UserService.loadUser(userRequest);

        assertThat(result).isNotNull();
        assertThat((String) result.getAttribute("email")).isEqualTo("test@gmail.com");
        verify(userRepository).save(any(User.class));
        verify(userPreferencesRepository).save(any(UserPreferences.class));
    }

    @Test
    @Disabled("Requires OAuth2 integration test setup with mock server")
    void shouldUpdateExistingUserOnOAuth2Login() {
        // Mock parent class to return oauth2User
        doReturn(oauth2User).when(oauth2UserService).loadUser(userRequest);
        doCallRealMethod().when(oauth2UserService).loadUser(userRequest);
        
        User existingUser = User.builder()
                .id(UUID.randomUUID())
                .email("test@gmail.com")
                .displayName("Existing User")
                .authProvider(User.AuthProvider.LOCAL)
                .build();

        when(userRepository.findByEmail("test@gmail.com")).thenReturn(Optional.of(existingUser));

        OAuth2User result = oauth2UserService.loadUser(userRequest);

        assertThat(result).isNotNull();
        assertThat((String) result.getAttribute("email")).isEqualTo("test@gmail.com");
        verify(userRepository).save(argThat(user -> 
            user.getAuthProvider() == User.AuthProvider.GOOGLE &&
            user.getLastLoginAt() != null
        ));
        verify(userPreferencesRepository, never()).save(any());
    }
}
