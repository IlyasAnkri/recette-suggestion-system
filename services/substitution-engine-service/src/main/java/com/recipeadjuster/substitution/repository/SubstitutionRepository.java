package com.recipeadjuster.substitution.repository;

import com.recipeadjuster.substitution.model.entity.Substitution;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubstitutionRepository extends MongoRepository<Substitution, String> {
    
    @Query("{ 'originalIngredientId': ?0, 'approvedAt': { $ne: null } }")
    List<Substitution> findApprovedByOriginalIngredientId(String originalIngredientId);
    
    @Query("{ 'originalIngredientId': ?0, 'dietaryTags': { $in: ?1 }, 'approvedAt': { $ne: null } }")
    List<Substitution> findApprovedByOriginalIngredientIdAndDietaryTags(String originalIngredientId, List<String> dietaryTags);
    
    @Query("{ 'aiGenerated': true, 'approvedAt': null }")
    List<Substitution> findPendingApproval();
    
    List<Substitution> findByOriginalIngredientName(String originalIngredientName);
}
