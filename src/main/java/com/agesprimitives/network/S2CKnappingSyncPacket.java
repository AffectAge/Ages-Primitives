package com.agesprimitives.network;

import com.agesprimitives.knapping.menu.KnappingMenu;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record S2CKnappingSyncPacket(int containerId, long removedMask, ItemStack result, boolean consumed) {
    public static void encode(S2CKnappingSyncPacket packet, FriendlyByteBuf buffer) {
        buffer.writeVarInt(packet.containerId);
        buffer.writeLong(packet.removedMask);
        buffer.writeItem(packet.result);
        buffer.writeBoolean(packet.consumed);
    }

    public static S2CKnappingSyncPacket decode(FriendlyByteBuf buffer) {
        return new S2CKnappingSyncPacket(buffer.readVarInt(), buffer.readLong(), buffer.readItem(), buffer.readBoolean());
    }

    public static void handle(S2CKnappingSyncPacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            Minecraft minecraft = Minecraft.getInstance();
            if (minecraft.player == null || !(minecraft.player.containerMenu instanceof KnappingMenu menu)) {
                return;
            }
            if (menu.containerId != packet.containerId) {
                return;
            }
            menu.clientApplySync(packet.removedMask, packet.result, packet.consumed);
        });
        context.setPacketHandled(true);
    }
}
