package com.recipeadjuster.userprofile.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "user_preferences")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserPreferences {
    
    @Id
    @Column(name = "user_id")
    private UUID userId;
    
    @OneToOne
    @MapsId
    @JoinColumn(name = "user_id")
    private User user;
    
    @ElementCollection
    @CollectionTable(name = "user_dietary_restrictions", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "restriction")
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private List<DietaryRestriction> dietaryRestrictions = new ArrayList<>();
    
    @ElementCollection
    @CollectionTable(name = "user_allergies", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "allergy")
    @Builder.Default
    private List<String> allergies = new ArrayList<>();
    
    @ElementCollection
    @CollectionTable(name = "user_cuisine_preferences", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "cuisine")
    @Builder.Default
    private List<String> cuisinePreferences = new ArrayList<>();
    
    @Enumerated(EnumType.STRING)
    @Column(name = "skill_level", nullable = false, length = 20)
    @Builder.Default
    private SkillLevel skillLevel = SkillLevel.BEGINNER;
    
    @Column(name = "household_size", nullable = false)
    @Builder.Default
    private Integer householdSize = 1;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "measurement_system", nullable = false, length = 10)
    @Builder.Default
    private MeasurementSystem measurementSystem = MeasurementSystem.METRIC;
    
    @Column(name = "default_servings", nullable = false)
    @Builder.Default
    private Integer defaultServings = 4;
    
    public enum DietaryRestriction {
        VEGETARIAN, VEGAN, GLUTEN_FREE, DAIRY_FREE, KETO, HALAL, KOSHER
    }
    
    public enum SkillLevel {
        BEGINNER, INTERMEDIATE, ADVANCED
    }
    
    public enum MeasurementSystem {
        METRIC, IMPERIAL
    }
}
