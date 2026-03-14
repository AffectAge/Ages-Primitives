package com.agesprimitives.integration.jei;

import com.agesprimitives.AgesPrimitives;
import com.agesprimitives.knapping.KnappingRecipe;
import com.agesprimitives.registry.ModRecipeTypes;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

import java.util.List;

@JeiPlugin
public class AgesPrimitivesJeiPlugin implements IModPlugin {
    public static final ResourceLocation PLUGIN_ID = new ResourceLocation(AgesPrimitives.MOD_ID, "jei_plugin");

    @Override
    public ResourceLocation getPluginUid() {
        return PLUGIN_ID;
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(new RockKnappingCategory(registration.getJeiHelpers().getGuiHelper()));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        Minecraft minecraft = Minecraft.getInstance();
        Level level = minecraft.level;
        if (level == null) {
            return;
        }

        List<KnappingRecipe> recipes = level.getRecipeManager().getAllRecipesFor(ModRecipeTypes.KNAPPING.get()).stream()
                .filter(recipe -> recipe.getKnappingType().equals(new ResourceLocation(AgesPrimitives.MOD_ID, "rock")))
                .toList();

        registration.addRecipes(RockKnappingCategory.RECIPE_TYPE, recipes);
    }
}
