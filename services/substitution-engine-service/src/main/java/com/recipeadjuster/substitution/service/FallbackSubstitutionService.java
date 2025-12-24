package com.recipeadjuster.substitution.service;

import com.recipeadjuster.substitution.model.SubstitutionSuggestion;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class FallbackSubstitutionService {

    private static final Map<String, List<SubstitutionSuggestion>> CURATED_SUBSTITUTIONS = new HashMap<>();

    static {
        CURATED_SUBSTITUTIONS.put("butter", List.of(
            SubstitutionSuggestion.builder()
                .substitute("coconut oil")
                .conversionRatio("1:1 (same amount)")
                .flavorImpact("minimal")
                .textureImpact("minimal")
                .explanation("Coconut oil has similar fat content and melting point, works well in baking")
                .build(),
            SubstitutionSuggestion.builder()
                .substitute("olive oil")
                .conversionRatio("3/4 cup for 1 cup butter")
                .flavorImpact("moderate")
                .textureImpact("minimal")
                .explanation("Olive oil provides moisture but adds distinct flavor, best for savory dishes")
                .build(),
            SubstitutionSuggestion.builder()
                .substitute("applesauce")
                .conversionRatio("1/2 cup for 1 cup butter")
                .flavorImpact("minimal")
                .textureImpact("moderate")
                .explanation("Reduces fat content, adds moisture, works well in cakes and muffins")
                .build()
        ));

        CURATED_SUBSTITUTIONS.put("eggs", List.of(
            SubstitutionSuggestion.builder()
                .substitute("flax egg")
                .conversionRatio("1 tbsp ground flax + 3 tbsp water per egg")
                .flavorImpact("minimal")
                .textureImpact("minimal")
                .explanation("Flax provides binding similar to eggs, works well in baking")
                .build(),
            SubstitutionSuggestion.builder()
                .substitute("applesauce")
                .conversionRatio("1/4 cup per egg")
                .flavorImpact("minimal")
                .textureImpact("moderate")
                .explanation("Adds moisture and binding, best for cakes and quick breads")
                .build(),
            SubstitutionSuggestion.builder()
                .substitute("banana")
                .conversionRatio("1/4 cup mashed banana per egg")
                .flavorImpact("moderate")
                .textureImpact("minimal")
                .explanation("Provides binding and moisture, adds banana flavor")
                .build()
        ));

        CURATED_SUBSTITUTIONS.put("milk", List.of(
            SubstitutionSuggestion.builder()
                .substitute("almond milk")
                .conversionRatio("1:1 (same amount)")
                .flavorImpact("minimal")
                .textureImpact("minimal")
                .explanation("Dairy-free alternative with similar consistency")
                .build(),
            SubstitutionSuggestion.builder()
                .substitute("oat milk")
                .conversionRatio("1:1 (same amount)")
                .flavorImpact("minimal")
                .textureImpact("minimal")
                .explanation("Creamy texture similar to dairy milk, neutral flavor")
                .build(),
            SubstitutionSuggestion.builder()
                .substitute("coconut milk")
                .conversionRatio("1:1 (same amount)")
                .flavorImpact("moderate")
                .textureImpact("minimal")
                .explanation("Rich and creamy, adds subtle coconut flavor")
                .build()
        ));

        CURATED_SUBSTITUTIONS.put("soy sauce", List.of(
            SubstitutionSuggestion.builder()
                .substitute("tamari sauce")
                .conversionRatio("1:1 (same amount)")
                .flavorImpact("minimal")
                .textureImpact("minimal")
                .explanation("Gluten-free alternative with similar umami flavor")
                .build(),
            SubstitutionSuggestion.builder()
                .substitute("coconut aminos")
                .conversionRatio("1:1 (same amount)")
                .flavorImpact("moderate")
                .textureImpact("minimal")
                .explanation("Soy-free, slightly sweeter but similar savory notes")
                .build(),
            SubstitutionSuggestion.builder()
                .substitute("worcestershire sauce + water")
                .conversionRatio("1:2 (diluted)")
                .flavorImpact("moderate")
                .textureImpact("minimal")
                .explanation("Mix 1 part Worcestershire with 2 parts water for similar depth")
                .build()
        ));

        CURATED_SUBSTITUTIONS.put("ginger", List.of(
            SubstitutionSuggestion.builder()
                .substitute("ground ginger")
                .conversionRatio("1/4 tsp ground per 1 tbsp fresh")
                .flavorImpact("minimal")
                .textureImpact("minimal")
                .explanation("Dried ginger is more concentrated, use less")
                .build(),
            SubstitutionSuggestion.builder()
                .substitute("galangal")
                .conversionRatio("1:1 (same amount)")
                .flavorImpact("moderate")
                .textureImpact("minimal")
                .explanation("Similar root, slightly more citrusy and pine-like")
                .build(),
            SubstitutionSuggestion.builder()
                .substitute("allspice")
                .conversionRatio("1/2 tsp per 1 tbsp fresh ginger")
                .flavorImpact("moderate")
                .textureImpact("minimal")
                .explanation("Warm spice that can mimic ginger's heat")
                .build()
        ));

        CURATED_SUBSTITUTIONS.put("sesame oil", List.of(
            SubstitutionSuggestion.builder()
                .substitute("olive oil + sesame seeds")
                .conversionRatio("1:1 oil + toasted seeds")
                .flavorImpact("minimal")
                .textureImpact("minimal")
                .explanation("Toast sesame seeds and add to olive oil for similar flavor")
                .build(),
            SubstitutionSuggestion.builder()
                .substitute("peanut oil")
                .conversionRatio("1:1 (same amount)")
                .flavorImpact("moderate")
                .textureImpact("minimal")
                .explanation("Nutty flavor, good for Asian cooking")
                .build(),
            SubstitutionSuggestion.builder()
                .substitute("walnut oil")
                .conversionRatio("1:1 (same amount)")
                .flavorImpact("moderate")
                .textureImpact("minimal")
                .explanation("Rich nutty flavor, works well in dressings")
                .build()
        ));
    }

    public List<SubstitutionSuggestion> getFallbackSubstitutions(String ingredient) {
        String normalizedIngredient = ingredient.toLowerCase().trim();
        
        List<SubstitutionSuggestion> suggestions = CURATED_SUBSTITUTIONS.get(normalizedIngredient);
        
        if (suggestions != null) {
            log.info("Returning {} curated substitutions for {}", suggestions.size(), ingredient);
            return suggestions;
        }
        
        log.warn("No curated substitutions found for {}", ingredient);
        return getGenericSubstitution(ingredient);
    }

    private List<SubstitutionSuggestion> getGenericSubstitution(String ingredient) {
        return List.of(
            SubstitutionSuggestion.builder()
                .substitute("No specific substitution available")
                .conversionRatio("N/A")
                .flavorImpact("unknown")
                .textureImpact("unknown")
                .explanation("Please consult a culinary expert or search online for " + ingredient + " substitutions")
                .build()
        );
    }
}
