package com.recipeadjuster.recipedatabase.repository;

import com.recipeadjuster.recipedatabase.model.Rating;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RatingRepository extends MongoRepository<Rating, String> {
    
    Optional<Rating> findByUserIdAndRecipeId(UUID userId, String recipeId);
    
    List<Rating> findByRecipeId(String recipeId);
}
