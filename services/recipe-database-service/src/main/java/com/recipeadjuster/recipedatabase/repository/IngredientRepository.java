package com.recipeadjuster.recipedatabase.repository;

import com.recipeadjuster.recipedatabase.model.Ingredient;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IngredientRepository extends MongoRepository<Ingredient, String> {
}
