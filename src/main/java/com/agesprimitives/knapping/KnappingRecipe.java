package com.agesprimitives.knapping;

import com.agesprimitives.registry.ModRecipeSerializers;
import com.agesprimitives.registry.ModRecipeTypes;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

public class KnappingRecipe implements Recipe<CraftingContainer> {
    private final ResourceLocation id;
    private final ResourceLocation knappingType;
    private final int width;
    private final int height;
    private final long requiredRemovedMask;
    private final ItemStack result;

    public KnappingRecipe(ResourceLocation id, ResourceLocation knappingType, int width, int height, long requiredRemovedMask, ItemStack result) {
        this.id = id;
        this.knappingType = knappingType;
        this.width = width;
        this.height = height;
        this.requiredRemovedMask = requiredRemovedMask;
        this.result = result;
    }

    public ResourceLocation getKnappingType() {
        return knappingType;
    }

    public int getPatternWidth() {
        return width;
    }

    public int getPatternHeight() {
        return height;
    }

    public long getRequiredRemovedMask() {
        return requiredRemovedMask;
    }

    public boolean matches(KnappingState state, ResourceLocation activeKnappingType) {
        return knappingType.equals(activeKnappingType)
                && state.width() == width
                && state.height() == height
                && state.removedMask() == requiredRemovedMask;
    }

    @Override
    public boolean matches(CraftingContainer container, Level level) {
        return false;
    }

    @Override
    public ItemStack assemble(CraftingContainer container, RegistryAccess registryAccess) {
        return result.copy();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return false;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess registryAccess) {
        return result.copy();
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        return NonNullList.create();
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipeSerializers.KNAPPING.get();
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipeTypes.KNAPPING.get();
    }
}
