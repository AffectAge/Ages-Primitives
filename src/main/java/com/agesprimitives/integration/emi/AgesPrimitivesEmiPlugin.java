package com.agesprimitives.integration.emi;

import com.agesprimitives.AgesPrimitives;
import com.agesprimitives.knapping.KnappingTypeManager;
import com.agesprimitives.knapping.KnappingRecipe;
import com.agesprimitives.registry.ModRecipeTypes;
import dev.emi.emi.api.EmiApi;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class AgesPrimitivesEmiPlugin implements EmiPlugin {
    private static final List<ResourceLocation> FALLBACK_TYPES = List.of(
            new ResourceLocation(AgesPrimitives.MOD_ID, "rock"),
            new ResourceLocation(AgesPrimitives.MOD_ID, "flint"),
            new ResourceLocation(AgesPrimitives.MOD_ID, "clay"),
            new ResourceLocation(AgesPrimitives.MOD_ID, "fire_clay"),
            new ResourceLocation(AgesPrimitives.MOD_ID, "leather"),
            new ResourceLocation(AgesPrimitives.MOD_ID, "bone")
    );
    private static final Map<ResourceLocation, EmiRecipeCategory> TYPE_TO_CATEGORY = new LinkedHashMap<>();

    @Override
    public void register(EmiRegistry registry) {
        TYPE_TO_CATEGORY.clear();
        for (ResourceLocation typeId : getKnownTypes()) {
            EmiRecipeCategory category = createCategory(typeId);
            TYPE_TO_CATEGORY.put(typeId, category);
            registry.addCategory(category);
            registry.addWorkstation(category, iconFor(typeId));
        }

        registry.getRecipeManager().getAllRecipesFor(ModRecipeTypes.KNAPPING.get()).forEach(recipe -> {
            EmiRecipeCategory category = TYPE_TO_CATEGORY.get(recipe.getKnappingType());
            if (category != null) {
                registry.addRecipe(new RockKnappingEmiRecipe(recipe, category));
            }
        });
    }

    public static boolean openKnappingRecipes(ResourceLocation knappingTypeId) {
        EmiRecipeCategory category = TYPE_TO_CATEGORY.get(knappingTypeId);
        if (category == null) {
            return false;
        }
        EmiApi.displayRecipeCategory(category);
        return true;
    }

    private static List<ResourceLocation> getKnownTypes() {
        List<ResourceLocation> loaded = KnappingTypeManager.INSTANCE.all().stream()
                .map(type -> type.id())
                .toList();
        if (!loaded.isEmpty()) {
            return loaded;
        }
        return FALLBACK_TYPES;
    }

    private static EmiRecipeCategory createCategory(ResourceLocation typeId) {
        ResourceLocation categoryId = new ResourceLocation(AgesPrimitives.MOD_ID, "knapping_" + typeId.getPath().replace('/', '_'));
        return new EmiRecipeCategory(categoryId, iconFor(typeId));
    }

    private static EmiStack iconFor(ResourceLocation typeId) {
        return KnappingTypeManager.INSTANCE.get(typeId)
                .map(type -> EmiStack.of(type.jeiIconItem()))
                .orElse(EmiStack.of(Items.FLINT));
    }
}
