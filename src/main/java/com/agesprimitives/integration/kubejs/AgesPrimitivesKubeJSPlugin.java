package com.agesprimitives.integration.kubejs;

import com.agesprimitives.AgesPrimitives;
import dev.latvian.mods.kubejs.KubeJSPlugin;
import dev.latvian.mods.kubejs.recipe.RecipeKey;
import dev.latvian.mods.kubejs.recipe.component.ItemComponents;
import dev.latvian.mods.kubejs.recipe.component.StringComponent;
import dev.latvian.mods.kubejs.recipe.schema.RecipeSchema;
import dev.latvian.mods.kubejs.recipe.schema.RegisterRecipeSchemasEvent;
import net.minecraft.resources.ResourceLocation;

public class AgesPrimitivesKubeJSPlugin extends KubeJSPlugin {
    private static final ResourceLocation KNAPPING_RECIPE_ID = new ResourceLocation(AgesPrimitives.MOD_ID, "knapping");

    private static final RecipeKey<String> KNAPPING_TYPE = StringComponent.ID.key("knapping_type");
    private static final RecipeKey<String[]> PATTERN = StringComponent.NON_EMPTY.asArray().key("pattern");
    private static final RecipeKey<dev.latvian.mods.kubejs.util.TinyMap<Character, String>> KEY =
            StringComponent.NON_EMPTY.asPatternKey().key("key");
    private static final RecipeKey<dev.latvian.mods.kubejs.item.OutputItem> RESULT = ItemComponents.OUTPUT.key("result");

    private static final RecipeSchema KNAPPING_SCHEMA = new RecipeSchema(
            KNAPPING_TYPE,
            PATTERN,
            KEY,
            RESULT
    ).constructor(
            RESULT,
            KNAPPING_TYPE,
            PATTERN,
            KEY
    ).uniqueOutputId(RESULT);

    @Override
    public void registerRecipeSchemas(RegisterRecipeSchemasEvent event) {
        event.register(KNAPPING_RECIPE_ID, KNAPPING_SCHEMA);
        event.mapRecipe("agesprimitivesKnapping", KNAPPING_RECIPE_ID);
        event.mapRecipe("knapping", KNAPPING_RECIPE_ID);
    }
}
