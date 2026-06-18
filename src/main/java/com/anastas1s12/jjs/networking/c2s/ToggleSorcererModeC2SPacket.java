package com.anastas1s12.jjs.networking.c2s;

import com.anastas1s12.jjs.networking.ModNetworking;
import com.anastas1s12.jjs.networking.s2c.SorcererModeSyncS2CPacket;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

import java.util.function.Supplier;

/**
 * C2S — sent when the player presses R to toggle Sorcerer Mode.
 *
 * The server flips the mode flag for that player and echoes back a
 * {@link SorcererModeSyncS2CPacket} so the client updates its local state.
 *
 * Payload: empty — the server just toggles whatever the current state is.
 */
public class ToggleSorcererModeC2SPacket {

    public ToggleSorcererModeC2SPacket() {}

    /** Decode constructor (no payload). */
    public ToggleSorcererModeC2SPacket(FriendlyByteBuf buf) {}

    /** Encode (nothing to write). */
    public void toBytes(FriendlyByteBuf buf) {}

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();
        ctx.enqueueWork(() -> {
            ServerPlayer player = ctx.getSender();
            if (player == null) return;

            // TODO: when a server-side SorcererModeCapability / PlayerData exists,
            //       flip the flag there instead of using a temporary tag approach.
            //
            // For now we use a simple persistent data tag on the player to track state:
            boolean current = player.getPersistentData()
                    .getBoolean("jjs_sorcerer_mode");
            boolean next = !current;
            player.getPersistentData().putBoolean("jjs_sorcerer_mode", next);

            // Echo the confirmed state back to the client
            ModNetworking.INSTANCE.send(
                    PacketDistributor.PLAYER.with(() -> player),
                    new SorcererModeSyncS2CPacket(next)
            );
        });
        ctx.setPacketHandled(true);
        return true;
    }
}
