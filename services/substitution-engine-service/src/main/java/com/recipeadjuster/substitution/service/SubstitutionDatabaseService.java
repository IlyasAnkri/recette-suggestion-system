package com.recipeadjuster.substitution.service;

import com.recipeadjuster.substitution.model.entity.Substitution;
import com.recipeadjuster.substitution.repository.SubstitutionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class SubstitutionDatabaseService {

    private final SubstitutionRepository repository;

    public List<Substitution> getApprovedSubstitutions(String ingredientName) {
        return repository.findByOriginalIngredientName(ingredientName).stream()
            .filter(s -> s.getApprovedAt() != null)
            .toList();
    }

    public List<Substitution> getApprovedSubstitutionsWithDietaryTags(String ingredientId, List<String> dietaryTags) {
        return repository.findApprovedByOriginalIngredientIdAndDietaryTags(ingredientId, dietaryTags);
    }

    public Substitution createSubstitution(Substitution substitution) {
        substitution.setCreatedAt(LocalDateTime.now());
        substitution.setUpdatedAt(LocalDateTime.now());
        
        if (substitution.getAiGenerated() == null) {
            substitution.setAiGenerated(false);
        }
        
        return repository.save(substitution);
    }

    public Optional<Substitution> updateSubstitution(String id, Substitution substitution) {
        return repository.findById(id)
            .map(existing -> {
                existing.setOriginalIngredientId(substitution.getOriginalIngredientId());
                existing.setSubstituteIngredientId(substitution.getSubstituteIngredientId());
                existing.setOriginalIngredientName(substitution.getOriginalIngredientName());
                existing.setSubstituteIngredientName(substitution.getSubstituteIngredientName());
                existing.setConversionRatio(substitution.getConversionRatio());
                existing.setCompatibilityScore(substitution.getCompatibilityScore());
                existing.setFlavorImpact(substitution.getFlavorImpact());
                existing.setTextureImpact(substitution.getTextureImpact());
                existing.setDietaryTags(substitution.getDietaryTags());
                existing.setExplanation(substitution.getExplanation());
                existing.setUpdatedAt(LocalDateTime.now());
                return repository.save(existing);
            });
    }

    public void deleteSubstitution(String id) {
        repository.deleteById(id);
    }

    public Optional<Substitution> approveSubstitution(String id) {
        return repository.findById(id)
            .map(substitution -> {
                substitution.setApprovedAt(LocalDateTime.now());
                substitution.setUpdatedAt(LocalDateTime.now());
                return repository.save(substitution);
            });
    }

    public List<Substitution> getPendingApproval() {
        return repository.findPendingApproval();
    }

    public List<Substitution> getAllSubstitutions() {
        return repository.findAll();
    }
}
