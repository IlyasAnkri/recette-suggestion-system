package com.recipeadjuster.shared.repository;

import com.recipeadjuster.shared.model.entity.Ingredient;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IngredientRepository extends MongoRepository<Ingredient, String> {
    Optional<Ingredient> findByName(String name);
    boolean existsByName(String name);
}
