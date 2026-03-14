package com.agesprimitives.network;

import com.agesprimitives.knapping.menu.KnappingMenu;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class C2STakeKnappingResultPacket {
    public static void encode(C2STakeKnappingResultPacket packet, FriendlyByteBuf buffer) {
    }

    public static C2STakeKnappingResultPacket decode(FriendlyByteBuf buffer) {
        return new C2STakeKnappingResultPacket();
    }

    public static void handle(C2STakeKnappingResultPacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player == null || !(player.containerMenu instanceof KnappingMenu menu)) {
                return;
            }
            menu.handleServerTakeResult(player, menu.getResultStack());
        });
        context.setPacketHandled(true);
    }
}
