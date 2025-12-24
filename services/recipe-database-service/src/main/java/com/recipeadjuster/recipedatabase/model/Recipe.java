package com.recipeadjuster.recipedatabase.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Document(collection = "recipes")
public class Recipe {
    
    @Id
    private String id;
    private String title;
    private String description;
    private List<RecipeIngredient> ingredients;
    private List<String> instructions;
    private String imageUrl;
    private String thumbnailUrl;
    private Integer prepTime;
    private Integer cookTime;
    private Integer servings;
    private String cuisine;
    private String difficulty;
    private UUID authorId;
    private String authorName;
    private Boolean isApproved;
    private LocalDateTime approvedAt;
    private Ratings ratings;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    public static class RecipeIngredient {
        private String ingredientId;
        private String name;
        private Double quantity;
        private String unit;
        
        public RecipeIngredient() {}
        
        public RecipeIngredient(String ingredientId, String name, Double quantity, String unit) {
            this.ingredientId = ingredientId;
            this.name = name;
            this.quantity = quantity;
            this.unit = unit;
        }
        
        public String getIngredientId() { return ingredientId; }
        public void setIngredientId(String ingredientId) { this.ingredientId = ingredientId; }
        
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        
        public Double getQuantity() { return quantity; }
        public void setQuantity(Double quantity) { this.quantity = quantity; }
        
        public String getUnit() { return unit; }
        public void setUnit(String unit) { this.unit = unit; }
    }
    
    public static class Ratings {
        private Double average;
        private Integer count;
        
        public Ratings() {}
        
        public Ratings(Double average, Integer count) {
            this.average = average;
            this.count = count;
        }
        
        public Double getAverage() { return average; }
        public void setAverage(Double average) { this.average = average; }
        
        public Integer getCount() { return count; }
        public void setCount(Integer count) { this.count = count; }
    }
    
    public Recipe() {}
    
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public List<RecipeIngredient> getIngredients() { return ingredients; }
    public void setIngredients(List<RecipeIngredient> ingredients) { this.ingredients = ingredients; }
    
    public List<String> getInstructions() { return instructions; }
    public void setInstructions(List<String> instructions) { this.instructions = instructions; }
    
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    
    public String getThumbnailUrl() { return thumbnailUrl; }
    public void setThumbnailUrl(String thumbnailUrl) { this.thumbnailUrl = thumbnailUrl; }
    
    public Integer getPrepTime() { return prepTime; }
    public void setPrepTime(Integer prepTime) { this.prepTime = prepTime; }
    
    public Integer getCookTime() { return cookTime; }
    public void setCookTime(Integer cookTime) { this.cookTime = cookTime; }
    
    public Integer getServings() { return servings; }
    public void setServings(Integer servings) { this.servings = servings; }
    
    public String getCuisine() { return cuisine; }
    public void setCuisine(String cuisine) { this.cuisine = cuisine; }
    
    public String getDifficulty() { return difficulty; }
    public void setDifficulty(String difficulty) { this.difficulty = difficulty; }
    
    public UUID getAuthorId() { return authorId; }
    public void setAuthorId(UUID authorId) { this.authorId = authorId; }
    
    public String getAuthorName() { return authorName; }
    public void setAuthorName(String authorName) { this.authorName = authorName; }
    
    public Boolean getIsApproved() { return isApproved; }
    public void setIsApproved(Boolean isApproved) { this.isApproved = isApproved; }
    
    public LocalDateTime getApprovedAt() { return approvedAt; }
    public void setApprovedAt(LocalDateTime approvedAt) { this.approvedAt = approvedAt; }
    
    public Ratings getRatings() { return ratings; }
    public void setRatings(Ratings ratings) { this.ratings = ratings; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
