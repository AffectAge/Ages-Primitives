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
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.Arrays;

@SuppressWarnings("removal")
public class RockKnappingCategory implements IRecipeCategory<KnappingRecipe> {
    private static final int JEI_CELL_SIZE = 14;
    private static final int GRID_X = 34;
    private static final int GRID_Y = 20;
    private static final int INPUT_X = 8;
    private static final int OUTPUT_X = 132;

    private final ResourceLocation knappingTypeId;
    private final RecipeType<KnappingRecipe> recipeType;
    private final IDrawable background;
    private final IDrawable icon;
    private final IDrawable slotBackground;

    public RockKnappingCategory(ResourceLocation knappingTypeId, RecipeType<KnappingRecipe> recipeType, IGuiHelper guiHelper) {
        this.knappingTypeId = knappingTypeId;
        this.recipeType = recipeType;
        this.background = guiHelper.createBlankDrawable(176, 110);
        this.slotBackground = guiHelper.getSlotDrawable();
        ItemStack iconStack = KnappingTypeManager.INSTANCE.get(knappingTypeId)
                .map(KnappingType::jeiIconItem)
                .orElse(new ItemStack(Items.FLINT));
        this.icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, iconStack);
    }

    @Override
    public RecipeType<KnappingRecipe> getRecipeType() {
        return recipeType;
    }

    @Override
    public Component getTitle() {
        String typeKey = knappingTypeId.getPath().replace('/', '_');
        return Component.translatable("jei.category.agesprimitives.knapping." + typeKey);
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
        int gridHeightPx = recipe.getPatternHeight() * JEI_CELL_SIZE;
        int slotY = GRID_Y + (gridHeightPx / 2) - 8;

        ItemStack[] inputs = KnappingTypeManager.INSTANCE.get(recipe.getKnappingType())
                .map(type -> Arrays.stream(type.input().ingredient().getItems()).map(ItemStack::copy).toArray(ItemStack[]::new))
                .orElse(new ItemStack[0]);

        if (inputs.length > 0) {
            builder.addSlot(RecipeIngredientRole.INPUT, INPUT_X, slotY)
                    .setBackground(slotBackground, -1, -1)
                    .addItemStacks(Arrays.asList(inputs));
        }

        builder.addSlot(RecipeIngredientRole.OUTPUT, OUTPUT_X, slotY)
                .setBackground(slotBackground, -1, -1)
                .addItemStack(recipe.getResultItem(net.minecraft.core.RegistryAccess.EMPTY));
    }

    @Override
    public void draw(KnappingRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics graphics, double mouseX, double mouseY) {
        int gridWidthPx = recipe.getPatternWidth() * JEI_CELL_SIZE;
        int gridHeightPx = recipe.getPatternHeight() * JEI_CELL_SIZE;

        graphics.fill(GRID_X - 2, GRID_Y - 2, GRID_X + gridWidthPx + 2, GRID_Y + gridHeightPx + 2, 0xFF1B1611);

        KnappingType type = KnappingTypeManager.INSTANCE.get(recipe.getKnappingType()).orElse(null);
        if (type != null && hasResource(type.activeCellsTexture())) {
            int texWidth = recipe.getPatternWidth() * 14;
            int texHeight = recipe.getPatternHeight() * 14;
            graphics.blit(type.activeCellsTexture(), GRID_X, GRID_Y, 0, 0, gridWidthPx, gridHeightPx, texWidth, texHeight);
        } else {
            graphics.fill(GRID_X, GRID_Y, GRID_X + gridWidthPx, GRID_Y + gridHeightPx, 0xFFA18A5D);
        }

        for (int y = 0; y < recipe.getPatternHeight(); y++) {
            for (int x = 0; x < recipe.getPatternWidth(); x++) {
                int index = y * recipe.getPatternWidth() + x;
                boolean removed = (recipe.getRequiredRemovedMask() & (1L << index)) != 0;
                if (!removed) {
                    continue;
                }

                int x0 = GRID_X + x * JEI_CELL_SIZE;
                int y0 = GRID_Y + y * JEI_CELL_SIZE;
                if (type != null && type.useDisabledTexture() && hasResource(type.disabledCellsTexture())) {
                    int u = x * 14;
                    int v = y * 14;
                    int texWidth = recipe.getPatternWidth() * 14;
                    int texHeight = recipe.getPatternHeight() * 14;
                    graphics.blit(type.disabledCellsTexture(), x0, y0, u, v, JEI_CELL_SIZE, JEI_CELL_SIZE, texWidth, texHeight);
                } else {
                    graphics.fill(x0, y0, x0 + JEI_CELL_SIZE, y0 + JEI_CELL_SIZE, 0xFF15110C);
                }
            }
        }
    }

    public ResourceLocation getKnappingTypeId() {
        return knappingTypeId;
    }

    private static boolean hasResource(ResourceLocation location) {
        return Minecraft.getInstance().getResourceManager().getResource(location).isPresent();
    }
}
