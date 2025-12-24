package com.recipeadjuster.substitution.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "substitutions")
@CompoundIndexes({
    @CompoundIndex(name = "ingredient_dietary_idx", def = "{'originalIngredientId': 1, 'dietaryTags': 1}")
})
public class Substitution {
    
    @Id
    private String id;
    
    @Indexed
    private String originalIngredientId;
    
    private String substituteIngredientId;
    
    private String originalIngredientName;
    
    private String substituteIngredientName;
    
    private String conversionRatio;
    
    private Double compatibilityScore;
    
    private String flavorImpact;
    
    private String textureImpact;
    
    @Indexed
    private List<String> dietaryTags;
    
    private String explanation;
    
    private Boolean aiGenerated;
    
    private LocalDateTime approvedAt;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
}
