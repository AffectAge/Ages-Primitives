package com.protivkultury.agesprimitives.knapping;

import com.protivkultury.agesprimitives.AgesPrimitivesMod;
import com.protivkultury.agesprimitives.registry.ModTags;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public enum KnappingType
{
    ROCK("rock", ModTags.ROCK_KNAPPING, 2, 1, false, false),
    CLAY("clay", ModTags.CLAY_KNAPPING, 5, 5, true, true);

    private final ResourceLocation id;
    private final TagKey<Item> inputTag;
    private final int requiredCount;
    private final int amountToConsume;
    private final boolean consumeAfterComplete;
    private final boolean useDisabledTexture;

    KnappingType(String id, TagKey<Item> inputTag, int requiredCount, int amountToConsume, boolean consumeAfterComplete, boolean useDisabledTexture)
    {
        this.id = new ResourceLocation(AgesPrimitivesMod.MOD_ID, id);
        this.inputTag = inputTag;
        this.requiredCount = requiredCount;
        this.amountToConsume = amountToConsume;
        this.consumeAfterComplete = consumeAfterComplete;
        this.useDisabledTexture = useDisabledTexture;
    }

    public ResourceLocation id()
    {
        return id;
    }

    public int requiredCount()
    {
        return requiredCount;
    }

    public int amountToConsume()
    {
        return amountToConsume;
    }

    public boolean consumeAfterComplete()
    {
        return consumeAfterComplete;
    }

    public boolean usesDisabledTexture()
    {
        return useDisabledTexture;
    }

    public boolean test(ItemStack stack)
    {
        return stack.is(inputTag);
    }

    @Nullable
    public static KnappingType fromStack(ItemStack stack)
    {
        for (KnappingType value : values())
        {
            if (value.test(stack))
            {
                return value;
            }
        }
        return null;
    }

    @Nullable
    public static KnappingType fromId(ResourceLocation id)
    {
        for (KnappingType value : values())
        {
            if (value.id.equals(id))
            {
                return value;
            }
        }
        return null;
    }
}
