package com.protivkultury.agesprimitives.registry;

import com.protivkultury.agesprimitives.AgesPrimitivesMod;
import com.protivkultury.agesprimitives.knapping.KnappingMenu;
import com.protivkultury.agesprimitives.knapping.KnappingType;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModMenus
{
    public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(ForgeRegistries.MENU_TYPES, AgesPrimitivesMod.MOD_ID);

    public static final RegistryObject<MenuType<KnappingMenu>> KNAPPING = MENUS.register("knapping", () -> IForgeMenuType.create((windowId, inv, buffer) -> {
        final InteractionHand hand = buffer.readEnum(InteractionHand.class);
        final int slot = buffer.readVarInt();
        final KnappingType type = buffer.readEnum(KnappingType.class);
        return new KnappingMenu(windowId, inv, hand, slot, type);
    }));
}
