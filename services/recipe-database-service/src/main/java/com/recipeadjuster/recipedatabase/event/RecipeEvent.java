package com.recipeadjuster.recipedatabase.event;

import java.time.LocalDateTime;
import java.util.UUID;

public class RecipeEvent {
    
    private String eventType;
    private String recipeId;
    private String recipeTitle;
    private UUID authorId;
    private String authorName;
    private String cuisine;
    private String difficulty;
    private LocalDateTime timestamp;
    private String correlationId;
    
    public RecipeEvent() {}
    
    public RecipeEvent(String eventType, String recipeId, String recipeTitle, UUID authorId, String authorName, String cuisine, String difficulty, String correlationId) {
        this.eventType = eventType;
        this.recipeId = recipeId;
        this.recipeTitle = recipeTitle;
        this.authorId = authorId;
        this.authorName = authorName;
        this.cuisine = cuisine;
        this.difficulty = difficulty;
        this.timestamp = LocalDateTime.now();
        this.correlationId = correlationId;
    }
    
    public String getEventType() { return eventType; }
    public void setEventType(String eventType) { this.eventType = eventType; }
    
    public String getRecipeId() { return recipeId; }
    public void setRecipeId(String recipeId) { this.recipeId = recipeId; }
    
    public String getRecipeTitle() { return recipeTitle; }
    public void setRecipeTitle(String recipeTitle) { this.recipeTitle = recipeTitle; }
    
    public UUID getAuthorId() { return authorId; }
    public void setAuthorId(UUID authorId) { this.authorId = authorId; }
    
    public String getAuthorName() { return authorName; }
    public void setAuthorName(String authorName) { this.authorName = authorName; }
    
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    
    public String getCuisine() { return cuisine; }
    public void setCuisine(String cuisine) { this.cuisine = cuisine; }
    
    public String getDifficulty() { return difficulty; }
    public void setDifficulty(String difficulty) { this.difficulty = difficulty; }
    
    public String getCorrelationId() { return correlationId; }
    public void setCorrelationId(String correlationId) { this.correlationId = correlationId; }
}
