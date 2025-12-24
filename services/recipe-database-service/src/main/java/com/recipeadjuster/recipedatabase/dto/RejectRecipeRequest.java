package com.recipeadjuster.recipedatabase.dto;

public class RejectRecipeRequest {
    private String reason;
    
    public RejectRecipeRequest() {}
    
    public RejectRecipeRequest(String reason) {
        this.reason = reason;
    }
    
    public String getReason() {
        return reason;
    }
    
    public void setReason(String reason) {
        this.reason = reason;
    }
}
