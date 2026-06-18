package com.anastas1s12.jjs.networking.c2s;

import com.anastas1s12.jjs.capability.CursedEnergyCapability;
import com.anastas1s12.jjs.networking.ModNetworking;
import com.anastas1s12.jjs.networking.s2c.CursedEnergySyncS2CPacket;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

import java.util.function.Supplier;

/**
 * Client -> Server packet to request a full CE data sync.
 * Used on login, respawn, or dimension change when client data may be stale.
 */
public class RequestCEDataC2SPacket {

    public RequestCEDataC2SPacket() {
    }

    public RequestCEDataC2SPacket(FriendlyByteBuf buf) {
    }

    public void toBytes(FriendlyByteBuf buf) {
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player != null) {
                player.getCapability(CursedEnergyCapability.CURSED_ENERGY_CAPABILITY).ifPresent(ce -> {
                    ModNetworking.INSTANCE.send(
                            PacketDistributor.PLAYER.with(() -> player),
                            new CursedEnergySyncS2CPacket(ce)
                    );
                });
            }
        });
        context.setPacketHandled(true);
        return true;
    }
}
