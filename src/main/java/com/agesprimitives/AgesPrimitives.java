package com.agesprimitives;

import com.agesprimitives.client.ClientSetup;
import com.agesprimitives.config.ModCommonConfig;
import com.agesprimitives.knapping.KnappingTypeManager;
import com.agesprimitives.network.PacketHandler;
import com.agesprimitives.registry.ModCreativeTabs;
import com.agesprimitives.registry.ModItems;
import com.agesprimitives.registry.ModMenus;
import com.agesprimitives.registry.ModRecipeSerializers;
import com.agesprimitives.registry.ModRecipeTypes;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(AgesPrimitives.MOD_ID)
public class AgesPrimitives {
    public static final String MOD_ID = "agesprimitives";

    public AgesPrimitives() {
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        ModItems.ITEMS.register(modBus);
        ModMenus.MENUS.register(modBus);
        ModRecipeTypes.RECIPE_TYPES.register(modBus);
        ModRecipeSerializers.RECIPE_SERIALIZERS.register(modBus);
        ModCreativeTabs.CREATIVE_MODE_TABS.register(modBus);

        modBus.addListener(this::onClientSetup);
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, ModCommonConfig.SPEC);

        PacketHandler.init();
        MinecraftForge.EVENT_BUS.addListener(this::onAddReloadListeners);
        MinecraftForge.EVENT_BUS.register(new ModEvents());
    }

    private void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(ClientSetup::init);
    }

    private void onAddReloadListeners(AddReloadListenerEvent event) {
        event.addListener(KnappingTypeManager.INSTANCE);
    }
}
