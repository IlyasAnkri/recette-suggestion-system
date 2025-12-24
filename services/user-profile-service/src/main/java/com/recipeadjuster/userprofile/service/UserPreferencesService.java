package com.recipeadjuster.userprofile.service;

import com.recipeadjuster.userprofile.model.dto.UpdatePreferencesRequest;
import com.recipeadjuster.userprofile.model.dto.UserProfileResponse;
import com.recipeadjuster.userprofile.model.entity.User;
import com.recipeadjuster.userprofile.model.entity.UserPreferences;
import com.recipeadjuster.userprofile.repository.UserPreferencesRepository;
import com.recipeadjuster.userprofile.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserPreferencesService {

    private final UserRepository userRepository;
    private final UserPreferencesRepository userPreferencesRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Transactional(readOnly = true)
    public UserProfileResponse getUserProfile(String userId) {
        User user = userRepository.findById(UUID.fromString(userId))
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        UserPreferences prefs = userPreferencesRepository.findById(user.getId())
                .orElseGet(() -> createDefaultPreferences(user));

        return buildProfileResponse(user, prefs);
    }

    @Transactional
    public UserProfileResponse updatePreferences(String userId, UpdatePreferencesRequest request) {
        User user = userRepository.findById(UUID.fromString(userId))
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        UserPreferences prefs = userPreferencesRepository.findById(user.getId())
                .orElseGet(() -> createDefaultPreferences(user));

        Map<String, Object> changes = new HashMap<>();

        if (request.getDietaryRestrictions() != null) {
            prefs.setDietaryRestrictions(request.getDietaryRestrictions());
            changes.put("dietaryRestrictions", request.getDietaryRestrictions());
        }
        if (request.getAllergies() != null) {
            prefs.setAllergies(request.getAllergies());
            changes.put("allergies", request.getAllergies());
        }
        if (request.getCuisinePreferences() != null) {
            prefs.setCuisinePreferences(request.getCuisinePreferences());
            changes.put("cuisinePreferences", request.getCuisinePreferences());
        }
        if (request.getSkillLevel() != null) {
            prefs.setSkillLevel(request.getSkillLevel());
            changes.put("skillLevel", request.getSkillLevel());
        }
        if (request.getHouseholdSize() != null) {
            prefs.setHouseholdSize(request.getHouseholdSize());
            changes.put("householdSize", request.getHouseholdSize());
        }
        if (request.getMeasurementSystem() != null) {
            prefs.setMeasurementSystem(request.getMeasurementSystem());
            changes.put("measurementSystem", request.getMeasurementSystem());
        }
        if (request.getDefaultServings() != null) {
            prefs.setDefaultServings(request.getDefaultServings());
            changes.put("defaultServings", request.getDefaultServings());
        }

        userPreferencesRepository.save(prefs);

        publishPreferenceUpdateEvent(user.getId(), changes);

        return buildProfileResponse(user, prefs);
    }

    private UserPreferences createDefaultPreferences(User user) {
        UserPreferences prefs = UserPreferences.builder()
                .user(user)
                .userId(user.getId())
                .build();
        return userPreferencesRepository.save(prefs);
    }

    private void publishPreferenceUpdateEvent(UUID userId, Map<String, Object> changes) {
        Map<String, Object> event = new HashMap<>();
        event.put("eventType", "user.preference.updated");
        event.put("userId", userId.toString());
        event.put("changes", changes);
        event.put("timestamp", System.currentTimeMillis());

        kafkaTemplate.send("user-events", userId.toString(), event);
        log.info("Published preference update event for user: {}", userId);
    }

    private UserProfileResponse buildProfileResponse(User user, UserPreferences prefs) {
        return UserProfileResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .displayName(user.getDisplayName())
                .avatarUrl(user.getAvatarUrl())
                .createdAt(user.getCreatedAt())
                .preferences(UserProfileResponse.PreferencesDto.builder()
                        .dietaryRestrictions(prefs.getDietaryRestrictions())
                        .allergies(prefs.getAllergies())
                        .cuisinePreferences(prefs.getCuisinePreferences())
                        .skillLevel(prefs.getSkillLevel())
                        .householdSize(prefs.getHouseholdSize())
                        .measurementSystem(prefs.getMeasurementSystem())
                        .defaultServings(prefs.getDefaultServings())
                        .build())
                .build();
    }
}
