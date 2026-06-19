package com.anastas1s12.jjs.networking.c2s;

import com.anastas1s12.jjs.client.DistortionClientState;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class NearbySorcererPacket {
    private final boolean detected;

    public NearbySorcererPacket(boolean detected) {
        this.detected = detected;
    }

    public static void encode(NearbySorcererPacket msg, FriendlyByteBuf buf) {
        buf.writeBoolean(msg.detected);
    }

    public static NearbySorcererPacket decode(FriendlyByteBuf buf) {
        return new NearbySorcererPacket(buf.readBoolean());
    }

    public static void handle(NearbySorcererPacket msg, net.minecraftforge.network.NetworkEvent.Context ctx) {
        ctx.enqueueWork(() -> {
            if (ctx.getDirection().getReceptionSide() == LogicalSide.CLIENT) {
                DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () ->
                        DistortionClientState.onPacket(msg.detected)
                );
            }
        });
        ctx.setPacketHandled(true);
    }
}