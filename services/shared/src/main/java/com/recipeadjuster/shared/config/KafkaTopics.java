package com.recipeadjuster.shared.config;

public final class KafkaTopics {
    public static final String INGREDIENT_SUBMITTED = "ingredient.submitted";
    public static final String RECIPE_MATCHED = "recipe.matched";
    public static final String SUBSTITUTION_REQUESTED = "substitution.requested";
    public static final String USER_PREFERENCE_UPDATED = "user.preference.updated";
    public static final String ANALYTICS_EVENT = "analytics.event";

    private KafkaTopics() {
        // Utility class
    }
}
