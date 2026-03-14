package com.agesprimitives.knapping;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;

import java.util.HashMap;
import java.util.Map;

public class KnappingRecipeSerializer implements RecipeSerializer<KnappingRecipe> {
    @Override
    public KnappingRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
        ResourceLocation knappingType = new ResourceLocation(GsonHelper.getAsString(json, "knapping_type"));
        JsonArray patternArray = GsonHelper.getAsJsonArray(json, "pattern");
        if (patternArray.size() == 0) {
            throw new JsonParseException("Knapping pattern cannot be empty: " + recipeId);
        }

        int height = patternArray.size();
        int width = patternArray.get(0).getAsString().length();
        if (width == 0) {
            throw new JsonParseException("Knapping pattern width cannot be zero: " + recipeId);
        }

        String[] pattern = new String[height];
        for (int y = 0; y < height; y++) {
            String row = patternArray.get(y).getAsString();
            if (row.length() != width) {
                throw new JsonParseException("All knapping pattern rows must be same width in " + recipeId);
            }
            pattern[y] = row;
        }

        Map<Character, Boolean> key = parseKey(GsonHelper.getAsJsonObject(json, "key"));
        long removedMask = toRemovedMask(pattern, key, recipeId);
        ItemStack result = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(json, "result"));
        return new KnappingRecipe(recipeId, knappingType, width, height, removedMask, result);
    }

    @Override
    public KnappingRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
        ResourceLocation knappingType = buffer.readResourceLocation();
        int width = buffer.readVarInt();
        int height = buffer.readVarInt();
        long removedMask = buffer.readLong();
        ItemStack result = buffer.readItem();
        return new KnappingRecipe(recipeId, knappingType, width, height, removedMask, result);
    }

    @Override
    public void toNetwork(FriendlyByteBuf buffer, KnappingRecipe recipe) {
        buffer.writeResourceLocation(recipe.getKnappingType());
        buffer.writeVarInt(recipe.getPatternWidth());
        buffer.writeVarInt(recipe.getPatternHeight());
        buffer.writeLong(recipe.getRequiredRemovedMask());
        buffer.writeItem(recipe.getResultItem(net.minecraft.core.RegistryAccess.EMPTY));
    }

    private static Map<Character, Boolean> parseKey(JsonObject keyJson) {
        Map<Character, Boolean> map = new HashMap<>();
        for (Map.Entry<String, JsonElement> entry : keyJson.entrySet()) {
            if (entry.getKey().length() != 1) {
                throw new JsonParseException("Knapping key entries must be one char: " + entry.getKey());
            }
            String state = entry.getValue().getAsString();
            if ("filled".equals(state)) {
                map.put(entry.getKey().charAt(0), Boolean.TRUE);
            } else if ("empty".equals(state)) {
                map.put(entry.getKey().charAt(0), Boolean.FALSE);
            } else {
                throw new JsonParseException("Unknown knapping key state: " + state);
            }
        }
        return map;
    }

    private static long toRemovedMask(String[] pattern, Map<Character, Boolean> key, ResourceLocation recipeId) {
        int width = pattern[0].length();
        long removedMask = 0L;
        for (int y = 0; y < pattern.length; y++) {
            String row = pattern[y];
            for (int x = 0; x < width; x++) {
                char token = row.charAt(x);
                Boolean filled = key.get(token);
                if (filled == null) {
                    throw new JsonParseException("Pattern token '" + token + "' is missing in key for " + recipeId);
                }
                if (!filled) {
                    int index = y * width + x;
                    if (index >= 64) {
                        throw new JsonParseException("Knapping grid larger than 64 cells is not supported in MVP");
                    }
                    removedMask |= 1L << index;
                }
            }
        }
        return removedMask;
    }
}
