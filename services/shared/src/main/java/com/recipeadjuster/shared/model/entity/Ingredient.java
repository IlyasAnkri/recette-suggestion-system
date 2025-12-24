package com.recipeadjuster.shared.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "ingredients")
public class Ingredient {
    
    @Id
    private String id;
    
    @Indexed(unique = true)
    private String name;
    
    @Indexed
    private List<String> aliases;
    
    @Indexed
    private IngredientCategory category;
    
    private NutritionalInfo nutritionalInfoPer100g;
    
    private List<String> commonUnits;
    
    private ShelfLife shelfLife;
    
    private List<String> flavorProfile;
    
    private List<Double> embedding;
    
    public enum IngredientCategory {
        PROTEIN, VEGETABLE, FRUIT, DAIRY, GRAIN, SPICE, CONDIMENT, OIL, SWEETENER, BEVERAGE, OTHER
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ShelfLife {
        private Integer pantry;
        private Integer refrigerated;
        private Integer frozen;
    }
}
