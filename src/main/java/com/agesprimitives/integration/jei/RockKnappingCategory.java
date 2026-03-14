package com.agesprimitives.integration.jei;

import com.agesprimitives.AgesPrimitives;
import com.agesprimitives.knapping.KnappingRecipe;
import com.agesprimitives.knapping.KnappingType;
import com.agesprimitives.knapping.KnappingTypeManager;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.Arrays;

@SuppressWarnings("removal")
public class RockKnappingCategory implements IRecipeCategory<KnappingRecipe> {
    public static final RecipeType<KnappingRecipe> RECIPE_TYPE = RecipeType.create(AgesPrimitives.MOD_ID, "rock_knapping", KnappingRecipe.class);

    private final IDrawable background;
    private final IDrawable icon;

    public RockKnappingCategory(IGuiHelper guiHelper) {
        this.background = guiHelper.createBlankDrawable(176, 110);
        ItemStack iconStack = KnappingTypeManager.INSTANCE.get(new ResourceLocation(AgesPrimitives.MOD_ID, "rock"))
                .map(KnappingType::jeiIconItem)
                .orElse(new ItemStack(Items.FLINT));
        this.icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, iconStack);
    }

    @Override
    public RecipeType<KnappingRecipe> getRecipeType() {
        return RECIPE_TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("jei.category.agesprimitives.rock_knapping");
    }

    @Override
    public IDrawable getBackground() {
        return background;
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, KnappingRecipe recipe, IFocusGroup focuses) {
        ItemStack[] inputs = KnappingTypeManager.INSTANCE.get(recipe.getKnappingType())
                .map(type -> Arrays.stream(type.input().ingredient().getItems()).map(ItemStack::copy).toArray(ItemStack[]::new))
                .orElse(new ItemStack[0]);

        if (inputs.length > 0) {
            builder.addSlot(RecipeIngredientRole.INPUT, 8, 46).addItemStacks(Arrays.asList(inputs));
        }

        builder.addSlot(RecipeIngredientRole.OUTPUT, 150, 46)
                .addItemStack(recipe.getResultItem(net.minecraft.core.RegistryAccess.EMPTY));
    }

    @Override
    public void draw(KnappingRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics graphics, double mouseX, double mouseY) {
        graphics.fill(20, 20, 20 + recipe.getPatternWidth() * 12 + 2, 20 + recipe.getPatternHeight() * 12 + 2, 0xFF2F261B);
        for (int y = 0; y < recipe.getPatternHeight(); y++) {
            for (int x = 0; x < recipe.getPatternWidth(); x++) {
                int index = y * recipe.getPatternWidth() + x;
                boolean removed = (recipe.getRequiredRemovedMask() & (1L << index)) != 0;
                int color = removed ? 0xFF15110C : 0xFFB09865;
                int x0 = 21 + x * 12;
                int y0 = 21 + y * 12;
                graphics.fill(x0, y0, x0 + 10, y0 + 10, color);
            }
        }
    }
}
