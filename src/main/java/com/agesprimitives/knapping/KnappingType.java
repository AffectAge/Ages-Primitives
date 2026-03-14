package com.agesprimitives.knapping;

import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.registries.ForgeRegistries;

public record KnappingType(
        ResourceLocation id,
        InputRequirement input,
        int amountToConsume,
        ResourceLocation clickSound,
        ResourceLocation resultTakeSound,
        boolean consumeAfterComplete,
        boolean useDisabledTexture,
        boolean spawnsParticles,
        int gridWidth,
        int gridHeight,
        ItemStack jeiIconItem,
        ResourceLocation activeCellsTexture,
        ResourceLocation disabledCellsTexture
) {
    public record InputRequirement(Ingredient ingredient, int count, boolean exactItem) {
        public static InputRequirement fromJson(JsonObject json) {
            JsonObject ingredientJson = json.getAsJsonObject("ingredient");
            Ingredient ingredient = Ingredient.fromJson(ingredientJson);
            int count = json.has("count") ? json.get("count").getAsInt() : 1;
            boolean exactItem = ingredientJson.has("item");
            return new InputRequirement(ingredient, Math.max(1, count), exactItem);
        }

        public boolean matches(ItemStack stack) {
            return ingredient.test(stack);
        }

        public boolean matchesExactly(ItemStack stack) {
            if (!exactItem) {
                return false;
            }
            return ingredient.test(stack);
        }
    }

    public boolean matches(ItemStack stack) {
        return input.matches(stack);
    }

    public SoundEvent resolveClickSound() {
        return ForgeRegistries.SOUND_EVENTS.getValue(clickSound);
    }

    public SoundEvent resolveResultTakeSound() {
        return ForgeRegistries.SOUND_EVENTS.getValue(resultTakeSound);
    }
}
