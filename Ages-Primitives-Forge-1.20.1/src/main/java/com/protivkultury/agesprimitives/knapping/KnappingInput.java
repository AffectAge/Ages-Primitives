package com.protivkultury.agesprimitives.knapping;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class KnappingInput implements Container
{
    private final KnappingType type;
    private final KnappingPattern pattern;
    private final ItemStack originalStack;

    public KnappingInput(KnappingType type, KnappingPattern pattern, ItemStack originalStack)
    {
        this.type = type;
        this.pattern = pattern;
        this.originalStack = originalStack;
    }

    public KnappingType type()
    {
        return type;
    }

    public KnappingPattern pattern()
    {
        return pattern;
    }

    public ItemStack originalStack()
    {
        return originalStack;
    }

    @Override
    public int getContainerSize()
    {
        return 1;
    }

    @Override
    public boolean isEmpty()
    {
        return originalStack.isEmpty();
    }

    @Override
    public ItemStack getItem(int slot)
    {
        return slot == 0 ? originalStack : ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeItem(int slot, int amount)
    {
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot)
    {
        return ItemStack.EMPTY;
    }

    @Override
    public void setItem(int slot, ItemStack stack)
    {
    }

    @Override
    public void setChanged()
    {
    }

    @Override
    public boolean stillValid(Player player)
    {
        return true;
    }

    @Override
    public void clearContent()
    {
    }
}
