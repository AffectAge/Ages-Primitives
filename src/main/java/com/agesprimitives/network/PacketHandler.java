package com.agesprimitives.network;

import com.agesprimitives.AgesPrimitives;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public final class PacketHandler {
    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(AgesPrimitives.MOD_ID, "network"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    private static int id = 0;

    private PacketHandler() {
    }

    public static void init() {
        CHANNEL.registerMessage(id++, C2SKnappingClickPacket.class, C2SKnappingClickPacket::encode, C2SKnappingClickPacket::decode, C2SKnappingClickPacket::handle);
        CHANNEL.registerMessage(id++, C2STakeKnappingResultPacket.class, C2STakeKnappingResultPacket::encode, C2STakeKnappingResultPacket::decode, C2STakeKnappingResultPacket::handle);
        CHANNEL.registerMessage(id++, S2CKnappingSyncPacket.class, S2CKnappingSyncPacket::encode, S2CKnappingSyncPacket::decode, S2CKnappingSyncPacket::handle);
    }
}
