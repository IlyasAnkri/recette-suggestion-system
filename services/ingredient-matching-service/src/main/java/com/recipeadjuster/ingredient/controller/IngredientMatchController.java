package com.recipeadjuster.ingredient.controller;

import com.recipeadjuster.ingredient.dto.IngredientMatchRequest;
import com.recipeadjuster.ingredient.dto.IngredientMatchResponse;
import com.recipeadjuster.ingredient.service.IngredientMatchService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/ingredients")
@RequiredArgsConstructor
public class IngredientMatchController {

    private final IngredientMatchService ingredientMatchService;

    @PostMapping("/match")
    public ResponseEntity<IngredientMatchResponse> matchIngredients(
            @Valid @RequestBody IngredientMatchRequest request,
            @RequestHeader(value = "X-User-Id", required = false) String userId,
            @RequestHeader(value = "X-Session-Id", required = false) String sessionId) {
        
        log.info("Received ingredient match request with {} ingredients", request.getIngredients().size());
        
        IngredientMatchResponse response = ingredientMatchService.matchRecipes(
            request, 
            userId, 
            sessionId
        );
        
        log.info("Found {} matching recipes", response.getTotalResults());
        return ResponseEntity.ok(response);
    }
}
