package com.anastas1s12.jjs.networking;

import com.anastas1s12.jjs.JujutsuSorcery;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

/**
 * Central networking registration for the mod.
 * All packets are registered here with unique IDs.
 */
public class ModNetworking {

    private static final String PROTOCOL_VERSION = "1.0";

    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            ResourceLocation.fromNamespaceAndPath(JujutsuSorcery.MOD_ID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    private static int packetId = 0;

    private static int id() {
        return packetId++;
    }

    /**
     * Register all packets. Must be called during mod initialization (FMLCommonSetupEvent).
     */
    public static void register() {
        // Server -> Client: Sync CE data to player HUD
        INSTANCE.messageBuilder(CursedEnergySyncS2CPacket.class, id(), NetworkDirection.PLAY_TO_CLIENT)
                .decoder(CursedEnergySyncS2CPacket::new)
                .encoder(CursedEnergySyncS2CPacket::toBytes)
                .consumerMainThread(CursedEnergySyncS2CPacket::handle)
                .add();

        // Client -> Server: Request CE data refresh (on login/dimension change)
        INSTANCE.messageBuilder(RequestCEDataC2SPacket.class, id(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(RequestCEDataC2SPacket::new)
                .encoder(RequestCEDataC2SPacket::toBytes)
                .consumerMainThread(RequestCEDataC2SPacket::handle)
                .add();
    }
}
