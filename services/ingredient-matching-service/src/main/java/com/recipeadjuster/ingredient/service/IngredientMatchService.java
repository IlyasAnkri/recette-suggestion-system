package com.recipeadjuster.ingredient.service;

import com.recipeadjuster.ingredient.dto.IngredientMatchRequest;
import com.recipeadjuster.ingredient.dto.IngredientMatchResponse;
import com.recipeadjuster.ingredient.kafka.IngredientEventProducer;
import com.recipeadjuster.shared.event.IngredientSubmittedEvent;
import com.recipeadjuster.shared.model.entity.Recipe;
import com.recipeadjuster.shared.repository.RecipeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class IngredientMatchService {

    private final RecipeRepository recipeRepository;
    private final MongoTemplate mongoTemplate;
    private final IngredientEventProducer eventProducer;

    public IngredientMatchResponse matchRecipes(
            IngredientMatchRequest request, 
            String userId, 
            String sessionId) {
        
        List<String> normalizedIngredients = normalizeIngredients(request.getIngredients());
        
        List<Recipe> recipes = findMatchingRecipes(request, normalizedIngredients);
        
        List<IngredientMatchResponse.RecipeMatch> matches = recipes.stream()
            .map(recipe -> calculateMatch(recipe, normalizedIngredients))
            .filter(match -> match.getMatchPercentage() >= request.getMinMatchPercentage())
            .sorted(Comparator.comparing(IngredientMatchResponse.RecipeMatch::getMatchPercentage).reversed())
            .limit(request.getMaxResults())
            .collect(Collectors.toList());
        
        publishEvent(request, userId, sessionId);
        
        return IngredientMatchResponse.builder()
            .matches(matches)
            .totalResults(matches.size())
            .build();
    }

    private List<String> normalizeIngredients(List<String> ingredients) {
        return ingredients.stream()
            .map(String::toLowerCase)
            .map(String::trim)
            .distinct()
            .collect(Collectors.toList());
    }

    private List<Recipe> findMatchingRecipes(
            IngredientMatchRequest request, 
            List<String> normalizedIngredients) {
        
        Query query = new Query();
        query.addCriteria(Criteria.where("isApproved").is(true));
        
        if (request.getFilters() != null) {
            IngredientMatchRequest.FilterOptions filters = request.getFilters();
            
            if (filters.getCuisines() != null && !filters.getCuisines().isEmpty()) {
                query.addCriteria(Criteria.where("cuisine").in(filters.getCuisines()));
            }
            
            if (filters.getMaxCookTime() != null) {
                query.addCriteria(Criteria.where("cookTime").lte(filters.getMaxCookTime()));
            }
            
            if (filters.getDifficulty() != null && !filters.getDifficulty().isEmpty()) {
                List<Recipe.Difficulty> difficulties = filters.getDifficulty().stream()
                    .map(String::toUpperCase)
                    .map(Recipe.Difficulty::valueOf)
                    .collect(Collectors.toList());
                query.addCriteria(Criteria.where("difficulty").in(difficulties));
            }
            
            if (filters.getDietary() != null && !filters.getDietary().isEmpty()) {
                query.addCriteria(Criteria.where("tags").all(filters.getDietary()));
            }
        }
        
        return mongoTemplate.find(query, Recipe.class);
    }

    private IngredientMatchResponse.RecipeMatch calculateMatch(
            Recipe recipe, 
            List<String> userIngredients) {
        
        List<String> requiredIngredients = recipe.getIngredients().stream()
            .filter(i -> i.getOptional() == null || !i.getOptional())
            .map(i -> i.getName().toLowerCase())
            .collect(Collectors.toList());
        
        List<String> matched = new ArrayList<>();
        List<String> missing = new ArrayList<>();
        
        for (String required : requiredIngredients) {
            if (userIngredients.stream().anyMatch(ui -> 
                    ui.contains(required) || required.contains(ui))) {
                matched.add(required);
            } else {
                missing.add(required);
            }
        }
        
        double matchPercentage = requiredIngredients.isEmpty() ? 0.0 :
            (matched.size() * 100.0) / requiredIngredients.size();
        
        return IngredientMatchResponse.RecipeMatch.builder()
            .recipeId(recipe.getId())
            .title(recipe.getTitle())
            .matchPercentage(Math.round(matchPercentage * 100.0) / 100.0)
            .matchedIngredients(matched)
            .missingIngredients(missing)
            .thumbnail(recipe.getImageUrl())
            .cuisine(recipe.getCuisine())
            .cookTime(recipe.getCookTime())
            .difficulty(recipe.getDifficulty().name())
            .build();
    }

    private void publishEvent(
            IngredientMatchRequest request, 
            String userId, 
            String sessionId) {
        
        IngredientSubmittedEvent.Preferences preferences = null;
        if (request.getFilters() != null) {
            preferences = IngredientSubmittedEvent.Preferences.builder()
                .dietaryRestrictions(request.getFilters().getDietary())
                .maxCookTime(request.getFilters().getMaxCookTime())
                .build();
        }
        
        IngredientSubmittedEvent.Payload payload = IngredientSubmittedEvent.Payload.builder()
            .userId(userId)
            .sessionId(sessionId != null ? sessionId : UUID.randomUUID().toString())
            .ingredients(request.getIngredients())
            .preferences(preferences)
            .build();
        
        IngredientSubmittedEvent event = IngredientSubmittedEvent.builder()
            .eventId(UUID.randomUUID().toString())
            .eventType("ingredient.submitted")
            .timestamp(Instant.now())
            .source("ingredient-matching-service")
            .payload(payload)
            .build();
        
        eventProducer.publishIngredientSubmitted(event);
    }
}
