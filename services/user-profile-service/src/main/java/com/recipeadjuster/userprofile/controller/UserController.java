package com.recipeadjuster.userprofile.controller;

import com.recipeadjuster.userprofile.model.dto.UpdatePreferencesRequest;
import com.recipeadjuster.userprofile.model.dto.UserProfileResponse;
import com.recipeadjuster.userprofile.service.UserPreferencesService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserPreferencesService userPreferencesService;

    @GetMapping("/me")
    public ResponseEntity<UserProfileResponse> getCurrentUser(
            @RequestHeader("X-User-Id") String userId) {
        UserProfileResponse profile = userPreferencesService.getUserProfile(userId);
        return ResponseEntity.ok(profile);
    }

    @PutMapping("/me/preferences")
    public ResponseEntity<UserProfileResponse> updatePreferences(
            @RequestHeader("X-User-Id") String userId,
            @Valid @RequestBody UpdatePreferencesRequest request) {
        UserProfileResponse profile = userPreferencesService.updatePreferences(userId, request);
        return ResponseEntity.ok(profile);
    }
}
