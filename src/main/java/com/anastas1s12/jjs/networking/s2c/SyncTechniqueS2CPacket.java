package com.anastas1s12.jjs.networking.s2c;

import com.anastas1s12.jjs.client.ClientTechniqueData;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * S2C — syncs the player's assigned technique to the client.
 *
 * Sent when:
 *   - A technique is assigned via {@code /jjs technique assign}.
 *   - The player logs in, respawns, or changes dimension.
 *
 * Payload: one UTF string — the technique ID (empty string = no technique).
 */
public class SyncTechniqueS2CPacket {

    private final String techniqueId;

    /**
     * @param techniqueId The ID of the assigned technique, or {@code ""} if none.
     */
    public SyncTechniqueS2CPacket(String techniqueId) {
        this.techniqueId = techniqueId != null ? techniqueId : "";
    }

    /** Decode constructor. */
    public SyncTechniqueS2CPacket(FriendlyByteBuf buf) {
        this.techniqueId = buf.readUtf(64); // ← max 64 chars for a technique ID
    }

    /** Encode. */
    public void toBytes(FriendlyByteBuf buf) {
        buf.writeUtf(techniqueId, 64); // ← max 64 chars for a technique ID
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();
        ctx.enqueueWork(() -> {
            // Client main thread — update the local technique cache
            ClientTechniqueData.setTechnique(techniqueId);
        });
        ctx.setPacketHandled(true);
        return true;
    }
}
