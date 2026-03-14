package com.agesprimitives.registry;

import com.agesprimitives.AgesPrimitives;
import com.agesprimitives.knapping.KnappingRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModRecipeTypes {
    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES = DeferredRegister.create(ForgeRegistries.RECIPE_TYPES, AgesPrimitives.MOD_ID);

    public static final RegistryObject<RecipeType<KnappingRecipe>> KNAPPING = RECIPE_TYPES.register("knapping", () -> new RecipeType<>() {
        @Override
        public String toString() {
            return new ResourceLocation(AgesPrimitives.MOD_ID, "knapping").toString();
        }
    });

    private ModRecipeTypes() {
    }
}
