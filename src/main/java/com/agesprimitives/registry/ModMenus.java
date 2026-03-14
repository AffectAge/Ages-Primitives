package com.agesprimitives.registry;

import com.agesprimitives.AgesPrimitives;
import com.agesprimitives.knapping.menu.KnappingMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModMenus {
    public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(ForgeRegistries.MENU_TYPES, AgesPrimitives.MOD_ID);

    public static final RegistryObject<MenuType<KnappingMenu>> KNAPPING = MENUS.register("knapping", () -> IForgeMenuType.create(KnappingMenu::client));

    private ModMenus() {
    }
}
