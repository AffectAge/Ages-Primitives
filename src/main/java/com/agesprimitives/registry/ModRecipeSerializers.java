package com.agesprimitives.registry;

import com.agesprimitives.AgesPrimitives;
import com.agesprimitives.knapping.KnappingRecipe;
import com.agesprimitives.knapping.KnappingRecipeSerializer;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModRecipeSerializers {
    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, AgesPrimitives.MOD_ID);

    public static final RegistryObject<RecipeSerializer<KnappingRecipe>> KNAPPING = RECIPE_SERIALIZERS.register("knapping", KnappingRecipeSerializer::new);

    private ModRecipeSerializers() {
    }
}
