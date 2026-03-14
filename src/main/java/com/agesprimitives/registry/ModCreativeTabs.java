package com.agesprimitives.registry;

import com.agesprimitives.AgesPrimitives;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public final class ModCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, AgesPrimitives.MOD_ID);

    public static final RegistryObject<CreativeModeTab> MAIN = CREATIVE_MODE_TABS.register("main", () -> CreativeModeTab.builder()
            .title(net.minecraft.network.chat.Component.translatable("itemGroup.agesprimitives.main"))
            .withTabsBefore(CreativeModeTabs.SPAWN_EGGS)
            .icon(() -> new ItemStack(ModItems.STONE_KNIFE_HEAD.get()))
            .displayItems((params, output) -> {
                output.accept(ModItems.STONE_KNIFE_HEAD.get());
                output.accept(ModItems.STONE_AXE_HEAD.get());
                output.accept(ModItems.STONE_SHOVEL_HEAD.get());
                output.accept(ModItems.STONE_HAMMER_HEAD.get());
                output.accept(ModItems.STONE_SPEAR_HEAD.get());
                output.accept(ModItems.STONE_JAVELIN_HEAD.get());
                output.accept(ModItems.STONE_HOE_HEAD.get());
                output.accept(ModItems.STONE_SCRAPER.get());
            })
            .build());

    private ModCreativeTabs() {
    }
}
