package com.anastas1s12.jjs.networking.s2c;

import com.anastas1s12.jjs.client.ClientSorcererState;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * S2C — sent by the server after processing a {@code ToggleSorcererModeC2SPacket}.
 *
 * Carries the confirmed sorcerer-mode boolean so the client can update
 * {@link ClientSorcererState} and show/hide the ability hotbar overlay.
 *
 * Payload: 1 boolean — the new active state.
 */
public class SorcererModeSyncS2CPacket {

    private final boolean active;

    public SorcererModeSyncS2CPacket(boolean active) {
        this.active = active;
    }

    /** Decode constructor. */
    public SorcererModeSyncS2CPacket(FriendlyByteBuf buf) {
        this.active = buf.readBoolean(); // ← reads the 1-byte boolean flag
    }

    /** Encode. */
    public void toBytes(FriendlyByteBuf buf) {
        buf.writeBoolean(active); // ← writes the 1-byte boolean flag
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();
        ctx.enqueueWork(() -> {
            // Runs on the client main thread
            ClientSorcererState.setSorcererModeActive(active);
        });
        ctx.setPacketHandled(true);
        return true;
    }
}
