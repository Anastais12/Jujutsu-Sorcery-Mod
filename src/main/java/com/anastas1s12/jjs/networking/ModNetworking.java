package com.anastas1s12.jjs.networking;

import com.anastas1s12.jjs.JujutsuSorcery;
import com.anastas1s12.jjs.networking.c2s.AssignAbilityToHotbarC2SPacket;
import com.anastas1s12.jjs.networking.c2s.RequestCEDataC2SPacket;
import com.anastas1s12.jjs.networking.c2s.ToggleSorcererModeC2SPacket;
import com.anastas1s12.jjs.networking.s2c.CursedEnergySyncS2CPacket;
import com.anastas1s12.jjs.networking.s2c.SorcererModeSyncS2CPacket;
import com.anastas1s12.jjs.networking.s2c.SyncAbilityHotbarS2CPacket;
import com.anastas1s12.jjs.networking.s2c.SyncTechniqueS2CPacket;
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

        // Client -> Server: Toggle sorcerer mode (R key press)
        INSTANCE.messageBuilder(ToggleSorcererModeC2SPacket.class, id(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(ToggleSorcererModeC2SPacket::new)
                .encoder(ToggleSorcererModeC2SPacket::toBytes)
                .consumerMainThread(ToggleSorcererModeC2SPacket::handle)
                .add();

        // Server -> Client: Confirm sorcerer mode on/off
        INSTANCE.messageBuilder(SorcererModeSyncS2CPacket.class, id(), NetworkDirection.PLAY_TO_CLIENT)
                .decoder(SorcererModeSyncS2CPacket::new)
                .encoder(SorcererModeSyncS2CPacket::toBytes)
                .consumerMainThread(SorcererModeSyncS2CPacket::handle)
                .add();

        // Server -> Client: Sync full ability hotbar assignment (9 slots)
        INSTANCE.messageBuilder(SyncAbilityHotbarS2CPacket.class, id(), NetworkDirection.PLAY_TO_CLIENT)
                .decoder(SyncAbilityHotbarS2CPacket::new)
                .encoder(SyncAbilityHotbarS2CPacket::toBytes)
                .consumerMainThread(SyncAbilityHotbarS2CPacket::handle)
                .add();

        // Server -> Client: Sync the player's assigned technique ID
        INSTANCE.messageBuilder(SyncTechniqueS2CPacket.class, id(), NetworkDirection.PLAY_TO_CLIENT)
                .decoder(SyncTechniqueS2CPacket::new)
                .encoder(SyncTechniqueS2CPacket::toBytes)
                .consumerMainThread(SyncTechniqueS2CPacket::handle)
                .add();

        // Client -> Server: Assign an ability to a hotbar slot (or clear it)
        INSTANCE.messageBuilder(AssignAbilityToHotbarC2SPacket.class, id(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(AssignAbilityToHotbarC2SPacket::new)
                .encoder(AssignAbilityToHotbarC2SPacket::toBytes)
                .consumerMainThread(AssignAbilityToHotbarC2SPacket::handle)
                .add();
    }
}
