package com.agesprimitives.client;

import com.agesprimitives.knapping.client.KnappingScreen;
import com.agesprimitives.registry.ModMenus;
import net.minecraft.client.gui.screens.MenuScreens;

public final class ClientSetup {
    private ClientSetup() {
    }

    public static void init() {
        MenuScreens.register(ModMenus.KNAPPING.get(), KnappingScreen::new);
    }
}
