package com.protivkultury.agesprimitives;

import com.protivkultury.agesprimitives.client.KnappingScreen;
import com.protivkultury.agesprimitives.network.ModNetwork;
import com.protivkultury.agesprimitives.registry.ModItems;
import com.protivkultury.agesprimitives.registry.ModMenus;
import com.protivkultury.agesprimitives.registry.ModRecipeSerializers;
import com.protivkultury.agesprimitives.registry.ModRecipeTypes;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterMenuScreensEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(AgesPrimitivesMod.MOD_ID)
public class AgesPrimitivesMod
{
    public static final String MOD_ID = "ages_primitives";

    public AgesPrimitivesMod()
    {
        final IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();

        ModItems.ITEMS.register(modBus);
        ModMenus.MENUS.register(modBus);
        ModRecipeTypes.RECIPE_TYPES.register(modBus);
        ModRecipeSerializers.RECIPE_SERIALIZERS.register(modBus);

        ModNetwork.register();

        modBus.addListener(this::onClientSetup);
        modBus.addListener(this::onRegisterScreens);

        MinecraftForge.EVENT_BUS.register(new ModEvents());
    }

    private void onClientSetup(FMLClientSetupEvent event)
    {
        // no-op
    }

    private void onRegisterScreens(RegisterMenuScreensEvent event)
    {
        event.register(ModMenus.KNAPPING.get(), KnappingScreen::new);
    }
}
