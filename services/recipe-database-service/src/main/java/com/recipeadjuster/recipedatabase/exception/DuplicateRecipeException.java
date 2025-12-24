package com.recipeadjuster.recipedatabase.exception;

public class DuplicateRecipeException extends RuntimeException {
    
    private final String duplicateRecipeTitle;
    
    public DuplicateRecipeException(String message, String duplicateRecipeTitle) {
        super(message);
        this.duplicateRecipeTitle = duplicateRecipeTitle;
    }
    
    public String getDuplicateRecipeTitle() {
        return duplicateRecipeTitle;
    }
}
