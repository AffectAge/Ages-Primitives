package com.agesprimitives.knapping;

import com.agesprimitives.registry.ModRecipeTypes;
import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeManager;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public final class KnappingRecipeIndex {
    private static final Logger LOGGER = LogUtils.getLogger();

    private static Map<ResourceLocation, Map<PatternKey, KnappingRecipe>> indexedRecipes = Map.of();

    private KnappingRecipeIndex() {
    }

    public static synchronized void invalidate() {
        indexedRecipes = Map.of();
    }

    public static synchronized Optional<KnappingRecipe> find(RecipeManager recipeManager, ResourceLocation knappingTypeId, int width, int height, long removedMask) {
        ensureBuilt(recipeManager);
        Map<PatternKey, KnappingRecipe> byMask = indexedRecipes.get(knappingTypeId);
        if (byMask == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(byMask.get(new PatternKey(width, height, removedMask)));
    }

    private static void ensureBuilt(RecipeManager recipeManager) {
        if (!indexedRecipes.isEmpty()) {
            return;
        }

        Map<ResourceLocation, Map<PatternKey, KnappingRecipe>> byType = new HashMap<>();
        for (KnappingRecipe recipe : recipeManager.getAllRecipesFor(ModRecipeTypes.KNAPPING.get())) {
            Map<PatternKey, KnappingRecipe> byMask = byType.computeIfAbsent(recipe.getKnappingType(), ignored -> new HashMap<>());
            PatternKey key = new PatternKey(recipe.getPatternWidth(), recipe.getPatternHeight(), recipe.getRequiredRemovedMask());
            KnappingRecipe previous = byMask.putIfAbsent(key, recipe);
            if (previous != null) {
                LOGGER.warn("Duplicate knapping pattern key for type {}: {} and {}", recipe.getKnappingType(), previous.getId(), recipe.getId());
            }
        }

        Map<ResourceLocation, Map<PatternKey, KnappingRecipe>> immutable = new HashMap<>();
        byType.forEach((typeId, map) -> immutable.put(typeId, Map.copyOf(map)));
        indexedRecipes = Map.copyOf(immutable);
    }

    private record PatternKey(int width, int height, long removedMask) {
    }
}
