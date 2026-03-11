package com.protivkultury.agesprimitives.registry;

import com.protivkultury.agesprimitives.AgesPrimitivesMod;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class ModTags
{
    public static final TagKey<Item> ANY_KNAPPING = item("any_knapping");
    public static final TagKey<Item> ROCK_KNAPPING = item("rock_knapping");
    public static final TagKey<Item> CLAY_KNAPPING = item("clay_knapping");

    private static TagKey<Item> item(String path)
    {
        return ItemTags.create(new ResourceLocation(AgesPrimitivesMod.MOD_ID, path));
    }
}
