package com.agesprimitives.integration.jei;

import com.agesprimitives.AgesPrimitives;
import com.agesprimitives.knapping.KnappingTypeManager;
import com.agesprimitives.knapping.KnappingRecipe;
import com.agesprimitives.registry.ModRecipeTypes;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.runtime.IJeiRuntime;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@JeiPlugin
public class AgesPrimitivesJeiPlugin implements IModPlugin {
    public static final ResourceLocation PLUGIN_ID = new ResourceLocation(AgesPrimitives.MOD_ID, "jei_plugin");
    private static final List<ResourceLocation> FALLBACK_TYPES = List.of(
            new ResourceLocation(AgesPrimitives.MOD_ID, "rock"),
            new ResourceLocation(AgesPrimitives.MOD_ID, "flint"),
            new ResourceLocation(AgesPrimitives.MOD_ID, "clay"),
            new ResourceLocation(AgesPrimitives.MOD_ID, "fire_clay"),
            new ResourceLocation(AgesPrimitives.MOD_ID, "leather"),
            new ResourceLocation(AgesPrimitives.MOD_ID, "bone")
    );
    private static volatile IJeiRuntime runtime;
    private static final Map<ResourceLocation, RecipeType<KnappingRecipe>> TYPE_TO_RECIPE_TYPE = new LinkedHashMap<>();

    @Override
    public ResourceLocation getPluginUid() {
        return PLUGIN_ID;
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        TYPE_TO_RECIPE_TYPE.clear();
        for (ResourceLocation typeId : getKnownTypes()) {
            RecipeType<KnappingRecipe> recipeType = recipeTypeFor(typeId);
            TYPE_TO_RECIPE_TYPE.put(typeId, recipeType);
            registration.addRecipeCategories(new RockKnappingCategory(typeId, recipeType, registration.getJeiHelpers().getGuiHelper()));
        }
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        Minecraft minecraft = Minecraft.getInstance();
        Level level = minecraft.level;
        if (level == null) {
            return;
        }

        Map<ResourceLocation, List<KnappingRecipe>> grouped = level.getRecipeManager().getAllRecipesFor(ModRecipeTypes.KNAPPING.get()).stream()
                .collect(Collectors.groupingBy(KnappingRecipe::getKnappingType));

        for (Map.Entry<ResourceLocation, RecipeType<KnappingRecipe>> entry : TYPE_TO_RECIPE_TYPE.entrySet()) {
            List<KnappingRecipe> recipes = grouped.getOrDefault(entry.getKey(), List.of());
            registration.addRecipes(entry.getValue(), recipes);
        }
    }

    @Override
    public void onRuntimeAvailable(IJeiRuntime jeiRuntime) {
        runtime = jeiRuntime;
    }

    @Override
    public void onRuntimeUnavailable() {
        runtime = null;
    }

    public static boolean openKnappingRecipes(ResourceLocation knappingTypeId) {
        IJeiRuntime jeiRuntime = runtime;
        if (jeiRuntime == null) {
            return false;
        }
        RecipeType<KnappingRecipe> recipeType = TYPE_TO_RECIPE_TYPE.get(knappingTypeId);
        if (recipeType == null) {
            return false;
        }
        jeiRuntime.getRecipesGui().showTypes(List.of(recipeType));
        return true;
    }

    private static List<ResourceLocation> getKnownTypes() {
        List<ResourceLocation> loaded = KnappingTypeManager.INSTANCE.all().stream()
                .map(type -> type.id())
                .toList();
        if (!loaded.isEmpty()) {
            return loaded;
        }
        return new ArrayList<>(FALLBACK_TYPES);
    }

    private static RecipeType<KnappingRecipe> recipeTypeFor(ResourceLocation knappingTypeId) {
        String path = "knapping_" + knappingTypeId.getPath().replace('/', '_');
        return RecipeType.create(AgesPrimitives.MOD_ID, path, KnappingRecipe.class);
    }
}
