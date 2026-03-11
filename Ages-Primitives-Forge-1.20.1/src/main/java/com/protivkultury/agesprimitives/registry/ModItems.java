package com.protivkultury.agesprimitives.registry;

import com.protivkultury.agesprimitives.AgesPrimitivesMod;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems
{
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, AgesPrimitivesMod.MOD_ID);

    public static final RegistryObject<Item> PRIMITIVE_ROCK = ITEMS.register("primitive_rock", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> STONE_KNIFE_HEAD = ITEMS.register("stone_knife_head", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> UNFIRED_INGOT_MOLD = ITEMS.register("unfired_ingot_mold", () -> new Item(new Item.Properties()));
}
