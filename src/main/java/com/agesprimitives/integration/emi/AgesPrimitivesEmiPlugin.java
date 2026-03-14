package com.agesprimitives.integration.emi;

import com.agesprimitives.AgesPrimitives;
import com.agesprimitives.knapping.KnappingRecipe;
import com.agesprimitives.registry.ModRecipeTypes;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;

public class AgesPrimitivesEmiPlugin implements EmiPlugin {
    public static final EmiRecipeCategory ROCK_KNAPPING = new EmiRecipeCategory(
            new ResourceLocation(AgesPrimitives.MOD_ID, "rock_knapping"),
            EmiStack.of(Items.FLINT)
    );

    @Override
    public void register(EmiRegistry registry) {
        registry.addCategory(ROCK_KNAPPING);
        registry.addWorkstation(ROCK_KNAPPING, EmiStack.of(Items.FLINT));

        registry.getRecipeManager().getAllRecipesFor(ModRecipeTypes.KNAPPING.get()).stream()
                .filter(recipe -> recipe.getKnappingType().equals(new ResourceLocation(AgesPrimitives.MOD_ID, "rock")))
                .map(RockKnappingEmiRecipe::new)
                .forEach(registry::addRecipe);
    }
}
