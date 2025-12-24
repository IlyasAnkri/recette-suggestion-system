package com.recipeadjuster.recipedatabase.controller;

import com.recipeadjuster.recipedatabase.dto.RejectRecipeRequest;
import com.recipeadjuster.recipedatabase.model.Recipe;
import com.recipeadjuster.recipedatabase.service.ModerationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/recipes")
public class AdminController {
    
    private static final Logger log = LoggerFactory.getLogger(AdminController.class);
    private final ModerationService moderationService;
    
    public AdminController(ModerationService moderationService) {
        this.moderationService = moderationService;
    }
    
    @GetMapping("/pending")
    public ResponseEntity<List<Recipe>> getPendingRecipes(
            @RequestHeader(value = "X-User-Role", required = false, defaultValue = "USER") String userRole) {
        
        log.info("GET /api/v1/admin/recipes/pending - Fetching pending recipes");
        
        if (!"MODERATOR".equals(userRole) && !"ADMIN".equals(userRole)) {
            return ResponseEntity.status(403).build();
        }
        
        List<Recipe> pending = moderationService.getPendingRecipes();
        return ResponseEntity.ok(pending);
    }
    
    @PostMapping("/{id}/approve")
    public ResponseEntity<Recipe> approveRecipe(
            @PathVariable String id,
            @RequestHeader(value = "X-User-Role", required = false, defaultValue = "USER") String userRole) {
        
        log.info("POST /api/v1/admin/recipes/{}/approve - Approving recipe", id);
        
        if (!"MODERATOR".equals(userRole) && !"ADMIN".equals(userRole)) {
            return ResponseEntity.status(403).build();
        }
        
        Recipe approved = moderationService.approveRecipe(id);
        return ResponseEntity.ok(approved);
    }
    
    @PostMapping("/{id}/reject")
    public ResponseEntity<Void> rejectRecipe(
            @PathVariable String id,
            @RequestBody RejectRecipeRequest request,
            @RequestHeader(value = "X-User-Role", required = false, defaultValue = "USER") String userRole) {
        
        log.info("POST /api/v1/admin/recipes/{}/reject - Rejecting recipe", id);
        
        if (!"MODERATOR".equals(userRole) && !"ADMIN".equals(userRole)) {
            return ResponseEntity.status(403).build();
        }
        
        moderationService.rejectRecipe(id, request.getReason());
        return ResponseEntity.noContent().build();
    }
}
