package com.recipeadjuster.substitution.controller;

import com.recipeadjuster.substitution.model.SubstitutionSuggestion;
import com.recipeadjuster.substitution.service.FallbackSubstitutionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/test")
@RequiredArgsConstructor
public class SimpleTestController {

    private final FallbackSubstitutionService fallbackService;

    @GetMapping("/health")
    public Map<String, Object> health() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("service", "substitution-engine-service");
        response.put("openai", "configured");
        response.put("eureka", "enabled");
        return response;
    }

    @GetMapping("/substitutions/{ingredient}")
    public Map<String, Object> getSubstitutions(@PathVariable String ingredient) {
        List<SubstitutionSuggestion> suggestions = fallbackService.getFallbackSubstitutions(ingredient);
        
        Map<String, Object> response = new HashMap<>();
        response.put("ingredient", ingredient);
        response.put("substitutions", suggestions);
        response.put("count", suggestions.size());
        response.put("source", "fallback-database");
        
        return response;
    }

    @GetMapping("/info")
    public Map<String, String> info() {
        Map<String, String> info = new HashMap<>();
        info.put("service", "Substitution Engine Service");
        info.put("version", "1.0.0");
        info.put("description", "AI-powered ingredient substitution service");
        info.put("openai_model", "gpt-3.5-turbo");
        info.put("eureka_enabled", "true");
        return info;
    }
}
