package com.anastas1s12.jjs.networking.s2c;

import com.anastas1s12.jjs.client.ClientAbilityData;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * S2C — syncs the player's full 9-slot ability hotbar assignment to the client.
 *
 * Sent on:
 *   - Player login / respawn / dimension change (so client always has fresh data).
 *   - After the server processes an {@code AssignAbilityToHotbarC2SPacket}.
 *
 * Payload: 9 UTF strings, one per slot. Empty string = slot is unoccupied.
 * Max length per ID: 64 characters.
 */
public class SyncAbilityHotbarS2CPacket {

    /** Must match {@link ClientAbilityData#HOTBAR_SLOTS}. */
    private static final int SLOTS = ClientAbilityData.HOTBAR_SLOTS; // 9

    private final String[] slotIds; // length == SLOTS

    /**
     * Construct from a full assignment array.
     * @param slotIds Array of 9 ability IDs. Null or "" = empty slot.
     */
    public SyncAbilityHotbarS2CPacket(String[] slotIds) {
        this.slotIds = new String[SLOTS];
        for (int i = 0; i < SLOTS; i++) {
            this.slotIds[i] = (slotIds != null && i < slotIds.length && slotIds[i] != null)
                    ? slotIds[i] : "";
        }
    }

    /** Decode constructor. */
    public SyncAbilityHotbarS2CPacket(FriendlyByteBuf buf) {
        this.slotIds = new String[SLOTS];
        for (int i = 0; i < SLOTS; i++) {
            slotIds[i] = buf.readUtf(64); // ← reads up to 64 chars per ability ID
        }
    }

    /** Encode. */
    public void toBytes(FriendlyByteBuf buf) {
        for (int i = 0; i < SLOTS; i++) {
            buf.writeUtf(slotIds[i] != null ? slotIds[i] : "", 64); // ← 64-char max per ID
        }
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();
        ctx.enqueueWork(() -> {
            // Runs on the client main thread
            ClientAbilityData.setHotbar(slotIds);
        });
        ctx.setPacketHandled(true);
        return true;
    }
}
