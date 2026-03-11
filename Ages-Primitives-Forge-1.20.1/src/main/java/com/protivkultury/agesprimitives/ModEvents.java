package com.protivkultury.agesprimitives;

import com.protivkultury.agesprimitives.knapping.KnappingMenu;
import com.protivkultury.agesprimitives.knapping.KnappingType;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.network.NetworkHooks;

public class ModEvents
{
    @SubscribeEvent
    public void onRightClickItem(PlayerInteractEvent.RightClickItem event)
    {
        final Player player = event.getEntity();
        if (player.level().isClientSide())
        {
            return;
        }

        final ItemStack stack = event.getItemStack();
        final KnappingType type = KnappingType.fromStack(stack);
        if (type == null || stack.getCount() < type.requiredCount())
        {
            return;
        }

        if (player instanceof ServerPlayer serverPlayer)
        {
            final int slot = event.getHand() == net.minecraft.world.InteractionHand.OFF_HAND ? -1 : serverPlayer.getInventory().selected;
            final MenuProvider provider = new SimpleMenuProvider(
                (windowId, inventory, ignored) -> new KnappingMenu(windowId, inventory, event.getHand(), slot, type),
                Component.translatable("screen.ages_primitives.knapping")
            );

            NetworkHooks.openScreen(serverPlayer, provider, buffer -> {
                buffer.writeEnum(event.getHand());
                buffer.writeVarInt(slot);
                buffer.writeEnum(type);
            });
            event.setCanceled(true);
            event.setCancellationResult(InteractionResult.SUCCESS);
        }
    }
}
