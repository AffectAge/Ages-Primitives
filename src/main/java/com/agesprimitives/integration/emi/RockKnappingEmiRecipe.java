package com.agesprimitives.integration.emi;

import com.agesprimitives.knapping.KnappingRecipe;
import com.agesprimitives.knapping.KnappingType;
import com.agesprimitives.knapping.KnappingTypeManager;
import dev.emi.emi.api.recipe.BasicEmiRecipe;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;

public class RockKnappingEmiRecipe extends BasicEmiRecipe {
    private static final ResourceLocation VANILLA_CONTAINER = new ResourceLocation("minecraft", "textures/gui/container/crafting_table.png");
    private static final int EMI_CELL_SIZE = 14;
    private static final int GRID_X = 34;
    private static final int GRID_Y = 20;
    private static final int INPUT_X = 8;
    private static final int OUTPUT_X = 132;

    private final KnappingRecipe recipe;

    public RockKnappingEmiRecipe(KnappingRecipe recipe) {
        super(AgesPrimitivesEmiPlugin.ROCK_KNAPPING, recipe.getId(), 176, 110);
        this.recipe = recipe;

        Ingredient ingredient = KnappingTypeManager.INSTANCE.get(recipe.getKnappingType())
                .map(KnappingType::input)
                .map(KnappingType.InputRequirement::ingredient)
                .orElse(Ingredient.EMPTY);

        this.inputs.add(EmiIngredient.of(ingredient));
        this.outputs.add(EmiStack.of(recipe.getResultItem(net.minecraft.core.RegistryAccess.EMPTY)));
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        int gridHeightPx = recipe.getPatternHeight() * EMI_CELL_SIZE;
        int slotY = GRID_Y + (gridHeightPx / 2) - 8;

        widgets.addSlot(inputs.get(0), INPUT_X, slotY)
                .customBackground(VANILLA_CONTAINER, 7, 83, 18, 18)
                .recipeContext(this);
        widgets.addSlot(outputs.get(0), OUTPUT_X, slotY)
                .customBackground(VANILLA_CONTAINER, 7, 83, 18, 18)
                .recipeContext(this);

        widgets.addDrawable(GRID_X - 2, GRID_Y - 2, recipe.getPatternWidth() * EMI_CELL_SIZE + 4, recipe.getPatternHeight() * EMI_CELL_SIZE + 4, (graphics, mouseX, mouseY, delta) -> {
            int gridWidthPx = recipe.getPatternWidth() * EMI_CELL_SIZE;
            int gridHeightPxInner = recipe.getPatternHeight() * EMI_CELL_SIZE;
            graphics.fill(GRID_X - 2, GRID_Y - 2, GRID_X + gridWidthPx + 2, GRID_Y + gridHeightPxInner + 2, 0xFF1B1611);

            KnappingType type = KnappingTypeManager.INSTANCE.get(recipe.getKnappingType()).orElse(null);
            if (type != null && hasResource(type.activeCellsTexture())) {
                int texWidth = recipe.getPatternWidth() * 14;
                int texHeight = recipe.getPatternHeight() * 14;
                graphics.blit(type.activeCellsTexture(), GRID_X, GRID_Y, 0, 0, gridWidthPx, gridHeightPxInner, texWidth, texHeight);
            } else {
                graphics.fill(GRID_X, GRID_Y, GRID_X + gridWidthPx, GRID_Y + gridHeightPxInner, 0xFFA18A5D);
            }

            for (int y = 0; y < recipe.getPatternHeight(); y++) {
                for (int x = 0; x < recipe.getPatternWidth(); x++) {
                    int index = y * recipe.getPatternWidth() + x;
                    boolean removed = (recipe.getRequiredRemovedMask() & (1L << index)) != 0;
                    if (!removed) {
                        continue;
                    }

                    int x0 = GRID_X + x * EMI_CELL_SIZE;
                    int y0 = GRID_Y + y * EMI_CELL_SIZE;
                    if (type != null && type.useDisabledTexture() && hasResource(type.disabledCellsTexture())) {
                        int u = x * 14;
                        int v = y * 14;
                        int texWidth = recipe.getPatternWidth() * 14;
                        int texHeight = recipe.getPatternHeight() * 14;
                        graphics.blit(type.disabledCellsTexture(), x0, y0, u, v, EMI_CELL_SIZE, EMI_CELL_SIZE, texWidth, texHeight);
                    } else {
                        graphics.fill(x0, y0, x0 + EMI_CELL_SIZE, y0 + EMI_CELL_SIZE, 0xFF15110C);
                    }
                }
            }
        });
    }

    private static boolean hasResource(ResourceLocation location) {
        return Minecraft.getInstance().getResourceManager().getResource(location).isPresent();
    }
}
