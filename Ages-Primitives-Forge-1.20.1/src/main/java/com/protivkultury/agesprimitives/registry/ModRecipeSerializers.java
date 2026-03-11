package com.protivkultury.agesprimitives.registry;

import com.protivkultury.agesprimitives.AgesPrimitivesMod;
import com.protivkultury.agesprimitives.knapping.KnappingRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModRecipeSerializers
{
    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, AgesPrimitivesMod.MOD_ID);

    public static final RegistryObject<RecipeSerializer<KnappingRecipe>> KNAPPING = RECIPE_SERIALIZERS.register("knapping", KnappingRecipe.Serializer::new);
}
