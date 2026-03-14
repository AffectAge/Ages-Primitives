package com.agesprimitives.registry;

import com.agesprimitives.AgesPrimitives;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, AgesPrimitives.MOD_ID);

    public static final RegistryObject<Item> STONE_KNIFE_HEAD = ITEMS.register("stone_knife_head", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> STONE_AXE_HEAD = ITEMS.register("stone_axe_head", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> STONE_SHOVEL_HEAD = ITEMS.register("stone_shovel_head", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> STONE_HAMMER_HEAD = ITEMS.register("stone_hammer_head", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> STONE_SPEAR_HEAD = ITEMS.register("stone_spear_head", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> STONE_JAVELIN_HEAD = ITEMS.register("stone_javelin_head", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> STONE_HOE_HEAD = ITEMS.register("stone_hoe_head", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> STONE_SCRAPER = ITEMS.register("stone_scraper", () -> new Item(new Item.Properties()));

    private ModItems() {
    }
}
