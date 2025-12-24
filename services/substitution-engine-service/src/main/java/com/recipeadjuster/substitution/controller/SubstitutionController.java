package com.recipeadjuster.substitution.controller;

import com.recipeadjuster.substitution.model.SubstitutionSuggestion;
import com.recipeadjuster.substitution.service.FallbackSubstitutionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/substitutions")
@CrossOrigin(origins = "*")
public class SubstitutionController {

    private final FallbackSubstitutionService fallbackService;

    @Autowired
    public SubstitutionController(FallbackSubstitutionService fallbackService) {
        this.fallbackService = fallbackService;
        log.info("SubstitutionController initialized");
    }

    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("Substitution Controller is working!");
    }

    @PostMapping("/suggest")
    public ResponseEntity<Map<String, Object>> suggestSubstitutions(@RequestBody SubstitutionRequest request) {
        log.info("Received substitution request for {} missing ingredients", request.getMissingIngredients().size());
        
        Map<String, Object> response = new HashMap<>();
        List<Map<String, Object>> allSubstitutions = new ArrayList<>();
        
        try {
            for (String ingredient : request.getMissingIngredients()) {
                log.info("Finding substitutions for: {}", ingredient);
                
                List<SubstitutionSuggestion> suggestions = fallbackService.getFallbackSubstitutions(ingredient);
                
                Map<String, Object> ingredientSubs = new HashMap<>();
                ingredientSubs.put("original", ingredient);
                ingredientSubs.put("suggestions", suggestions);
                allSubstitutions.add(ingredientSubs);
            }
            
            response.put("substitutions", allSubstitutions);
            response.put("success", true);
            response.put("aiGenerated", false);
            response.put("message", "Using curated substitution database");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Error processing substitution request", e);
            response.put("success", false);
            response.put("error", e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    public static class SubstitutionRequest {
        private List<String> missingIngredients;
        private List<String> availableIngredients;
        private List<String> dietaryRestrictions;

        public List<String> getMissingIngredients() {
            return missingIngredients != null ? missingIngredients : Collections.emptyList();
        }

        public void setMissingIngredients(List<String> missingIngredients) {
            this.missingIngredients = missingIngredients;
        }

        public List<String> getAvailableIngredients() {
            return availableIngredients != null ? availableIngredients : Collections.emptyList();
        }

        public void setAvailableIngredients(List<String> availableIngredients) {
            this.availableIngredients = availableIngredients;
        }

        public List<String> getDietaryRestrictions() {
            return dietaryRestrictions != null ? dietaryRestrictions : Collections.emptyList();
        }

        public void setDietaryRestrictions(List<String> dietaryRestrictions) {
            this.dietaryRestrictions = dietaryRestrictions;
        }
    }
}
