package com.recipeadjuster.recipedatabase.dto;

public class RateRecipeRequest {
    private Integer rating;
    
    public RateRecipeRequest() {}
    
    public RateRecipeRequest(Integer rating) {
        this.rating = rating;
    }
    
    public Integer getRating() {
        return rating;
    }
    
    public void setRating(Integer rating) {
        this.rating = rating;
    }
}
