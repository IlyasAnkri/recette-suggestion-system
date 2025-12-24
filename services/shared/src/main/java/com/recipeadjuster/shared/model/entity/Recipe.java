package com.recipeadjuster.shared.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.index.TextIndexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "recipes")
@CompoundIndexes({
    @CompoundIndex(name = "cuisine_approved_idx", def = "{'cuisine': 1, 'isApproved': 1}"),
    @CompoundIndex(name = "difficulty_approved_idx", def = "{'difficulty': 1, 'isApproved': 1}"),
    @CompoundIndex(name = "rating_approved_idx", def = "{'ratings.average': -1, 'isApproved': 1}"),
    @CompoundIndex(name = "tags_approved_idx", def = "{'tags': 1, 'isApproved': 1}"),
    @CompoundIndex(name = "cuisine_cooktime_idx", def = "{'cuisine': 1, 'cookTime': 1}")
})
public class Recipe {
    
    @Id
    private String id;
    
    @TextIndexed(weight = 2)
    private String title;
    
    @Indexed(unique = true)
    private String slug;
    
    @TextIndexed
    private String description;
    
    private List<RecipeIngredient> ingredients;
    
    private List<Instruction> instructions;
    
    private Integer prepTime;
    
    private Integer cookTime;
    
    private Integer servings;
    
    @Indexed
    private Difficulty difficulty;
    
    @Indexed
    private String cuisine;
    
    private List<String> categories;
    
    @Indexed
    private List<String> tags;
    
    private NutritionalInfo nutritionalInfo;
    
    private String imageUrl;
    
    private String videoUrl;
    
    private Author author;
    
    private Ratings ratings;
    
    @Indexed
    private Boolean isApproved;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
    
    private List<Double> embedding;
    
    public enum Difficulty {
        EASY, MEDIUM, HARD
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RecipeIngredient {
        private String name;
        private Double quantity;
        private String unit;
        private Boolean optional;
        private String notes;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Instruction {
        private Integer stepNumber;
        private String description;
        private Integer durationMinutes;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Author {
        private UUID userId;
        private String name;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Ratings {
        private Double average;
        private Integer count;
    }
}
