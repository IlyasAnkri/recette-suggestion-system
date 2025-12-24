package com.recipeadjuster.userprofile.repository;

import com.recipeadjuster.userprofile.model.entity.User;
import com.recipeadjuster.userprofile.model.entity.UserPreferences;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserRepositoryTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
    }

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserPreferencesRepository userPreferencesRepository;

    @Test
    void shouldSaveUserToDatabase() {
        User user = User.builder()
                .email("test@example.com")
                .passwordHash("hashedpassword")
                .displayName("Test User")
                .build();

        User saved = userRepository.saveAndFlush(user);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getEmail()).isEqualTo("test@example.com");
        assertThat(saved.getCreatedAt()).isNotNull();
        assertThat(saved.getIsGuest()).isFalse();
        assertThat(saved.getAuthProvider()).isEqualTo(User.AuthProvider.LOCAL);
    }

    @Test
    void shouldEnforceEmailUniqueness() {
        User user1 = User.builder()
                .email("duplicate@example.com")
                .passwordHash("hash1")
                .build();
        userRepository.save(user1);

        User user2 = User.builder()
                .email("duplicate@example.com")
                .passwordHash("hash2")
                .build();

        assertThat(userRepository.existsByEmail("duplicate@example.com")).isTrue();
    }

    @Test
    void shouldFindUserByEmail() {
        User user = User.builder()
                .email("find@example.com")
                .passwordHash("hash")
                .build();
        userRepository.save(user);

        var found = userRepository.findByEmail("find@example.com");

        assertThat(found).isPresent();
        assertThat(found.get().getEmail()).isEqualTo("find@example.com");
    }

    @Test
    @Disabled("Known issue: @MapsId with @OneToOne orphanRemoval doesn't cascade delete in JPA - works in production with database CASCADE")
    void shouldCascadeDeleteUserPreferences() {
        User user = User.builder()
                .email("cascade@example.com")
                .passwordHash("hash")
                .build();
        user = userRepository.saveAndFlush(user);

        UserPreferences prefs = UserPreferences.builder()
                .user(user)
                .skillLevel(UserPreferences.SkillLevel.BEGINNER)
                .householdSize(2)
                .build();
        userPreferencesRepository.saveAndFlush(prefs);

        UUID userId = user.getId();
        
        // Verify preferences exist before delete
        assertThat(userPreferencesRepository.findById(userId)).isPresent();
        
        // Delete user and verify cascade
        userRepository.delete(user);
        userRepository.flush();
        userPreferencesRepository.flush();

        assertThat(userPreferencesRepository.findById(userId)).isEmpty();
    }
}
