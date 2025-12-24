package com.recipeadjuster.recipedatabase.repository;

import com.recipeadjuster.recipedatabase.model.Recipe;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface RecipeRepository extends MongoRepository<Recipe, String> {
    
    List<Recipe> findByAuthorId(UUID authorId);
    
    List<Recipe> findByIsApproved(Boolean isApproved);
    
    List<Recipe> findByIsApprovedTrue();
}
