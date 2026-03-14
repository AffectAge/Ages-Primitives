package com.agesprimitives.network;

import com.agesprimitives.knapping.menu.KnappingMenu;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record C2SKnappingClickPacket(int x, int y) {
    public static void encode(C2SKnappingClickPacket packet, FriendlyByteBuf buffer) {
        buffer.writeVarInt(packet.x);
        buffer.writeVarInt(packet.y);
    }

    public static C2SKnappingClickPacket decode(FriendlyByteBuf buffer) {
        return new C2SKnappingClickPacket(buffer.readVarInt(), buffer.readVarInt());
    }

    public static void handle(C2SKnappingClickPacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player == null || !(player.containerMenu instanceof KnappingMenu menu)) {
                return;
            }
            menu.handleServerClick(player, packet.x, packet.y);
        });
        context.setPacketHandled(true);
    }
}
