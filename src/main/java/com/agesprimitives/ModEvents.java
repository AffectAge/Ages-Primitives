package com.agesprimitives;

import com.agesprimitives.config.ModCommonConfig;
import com.agesprimitives.knapping.KnappingRecipeIndex;
import com.agesprimitives.knapping.KnappingType;
import com.agesprimitives.knapping.KnappingTypeManager;
import com.agesprimitives.knapping.menu.KnappingMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.OnDatapackSyncEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.network.NetworkHooks;

public class ModEvents {
    @SubscribeEvent
    public void onRightClickItem(PlayerInteractEvent.RightClickItem event) {
        Player player = event.getEntity();
        if (player.level().isClientSide() || player.isSpectator()) {
            return;
        }

        KnappingType type = ModCommonConfig.findTypeForStack(event.getItemStack())
                .flatMap(typeId -> KnappingTypeManager.INSTANCE.get(typeId))
                .orElse(null);
        if (type == null) {
            return;
        }

        int requiredCount = ModCommonConfig.getRequiredCount(type.id(), type.input().count());
        if (event.getItemStack().getCount() < requiredCount) {
            player.displayClientMessage(Component.translatable("message.agesprimitives.requires_more_material"), true);
            return;
        }

        ServerPlayer serverPlayer = (ServerPlayer) player;
        NetworkHooks.openScreen(serverPlayer, new KnappingMenu.Provider(type.id(), event.getHand()), buf -> {
            buf.writeResourceLocation(type.id());
            buf.writeEnum(event.getHand());
            buf.writeVarInt(type.gridWidth());
            buf.writeVarInt(type.gridHeight());
            buf.writeBoolean(type.useDisabledTexture());
            buf.writeResourceLocation(type.activeCellsTexture());
            buf.writeResourceLocation(type.disabledCellsTexture());
        });
        if (serverPlayer.containerMenu instanceof KnappingMenu menu) {
            menu.onOpened(serverPlayer);
        }

        event.setCanceled(true);
        event.setCancellationResult(InteractionResult.CONSUME);
    }

    @SubscribeEvent
    public void onDatapackSync(OnDatapackSyncEvent event) {
        KnappingRecipeIndex.invalidate();
        if (event.getPlayer() != null) {
            syncPlayerMenu(event.getPlayer());
            return;
        }
        for (ServerPlayer player : event.getPlayerList().getPlayers()) {
            syncPlayerMenu(player);
        }
    }

    private void syncPlayerMenu(ServerPlayer player) {
        if (player.containerMenu instanceof KnappingMenu menu) {
            menu.onDatapackReload(player);
        }
    }
}
