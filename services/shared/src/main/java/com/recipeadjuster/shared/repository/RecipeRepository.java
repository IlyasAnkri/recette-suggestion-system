package com.recipeadjuster.shared.repository;

import com.recipeadjuster.shared.model.entity.Recipe;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RecipeRepository extends MongoRepository<Recipe, String> {
    Optional<Recipe> findBySlug(String slug);
    boolean existsBySlug(String slug);
}
