package com.agesprimitives.integration.emi;

import com.agesprimitives.knapping.KnappingRecipe;
import com.agesprimitives.knapping.KnappingType;
import com.agesprimitives.knapping.KnappingTypeManager;
import dev.emi.emi.api.recipe.BasicEmiRecipe;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;

public class RockKnappingEmiRecipe extends BasicEmiRecipe {
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
        widgets.addSlot(inputs.get(0), 8, 46).recipeContext(this);
        widgets.addSlot(outputs.get(0), 150, 46).recipeContext(this);

        widgets.addDrawable(20, 20, recipe.getPatternWidth() * 12 + 2, recipe.getPatternHeight() * 12 + 2, (graphics, mouseX, mouseY, delta) -> {
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
        });
    }
}
