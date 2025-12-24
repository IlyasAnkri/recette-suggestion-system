package com.recipeadjuster.ingredient.service;

import com.recipeadjuster.shared.model.entity.Ingredient;
import com.recipeadjuster.shared.repository.IngredientRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SemanticMatchService {

    private final IngredientRepository ingredientRepository;
    private final EmbeddingService embeddingService;
    
    private static final double SIMILARITY_THRESHOLD = 0.8;

    public List<String> expandIngredients(List<String> userIngredients) {
        Set<String> expandedIngredients = new HashSet<>(userIngredients);
        
        for (String userIngredient : userIngredients) {
            List<Double> userEmbedding = embeddingService.embed(userIngredient);
            
            List<Ingredient> allIngredients = ingredientRepository.findAll();
            
            for (Ingredient ingredient : allIngredients) {
                if (ingredient.getEmbedding() != null && !ingredient.getEmbedding().isEmpty()) {
                    double similarity = cosineSimilarity(userEmbedding, ingredient.getEmbedding());
                    
                    if (similarity >= SIMILARITY_THRESHOLD) {
                        expandedIngredients.add(ingredient.getName());
                        log.debug("Semantic match: '{}' -> '{}' (similarity: {})", 
                            userIngredient, ingredient.getName(), similarity);
                    }
                }
            }
            
            Optional<Ingredient> exactMatch = ingredientRepository.findByName(userIngredient.toLowerCase());
            if (exactMatch.isPresent()) {
                expandedIngredients.add(exactMatch.get().getName());
                expandedIngredients.addAll(exactMatch.get().getAliases());
            }
        }
        
        return new ArrayList<>(expandedIngredients);
    }

    public double cosineSimilarity(List<Double> vec1, List<Double> vec2) {
        if (vec1.size() != vec2.size()) {
            throw new IllegalArgumentException("Vectors must have the same dimensions");
        }
        
        double dotProduct = 0.0;
        double norm1 = 0.0;
        double norm2 = 0.0;
        
        for (int i = 0; i < vec1.size(); i++) {
            dotProduct += vec1.get(i) * vec2.get(i);
            norm1 += vec1.get(i) * vec1.get(i);
            norm2 += vec2.get(i) * vec2.get(i);
        }
        
        if (norm1 == 0.0 || norm2 == 0.0) {
            return 0.0;
        }
        
        return dotProduct / (Math.sqrt(norm1) * Math.sqrt(norm2));
    }
}
