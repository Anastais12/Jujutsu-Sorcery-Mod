package com.anastas1s12.jjs.networking.c2s;

import com.anastas1s12.jjs.ability.PlayerTechniqueData;
import com.anastas1s12.jjs.ability.TechniqueRegistry;
import com.anastas1s12.jjs.ability.Technique;
import com.anastas1s12.jjs.networking.ModNetworking;
import com.anastas1s12.jjs.networking.s2c.SyncAbilityHotbarS2CPacket;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

import java.util.function.Supplier;

/**
 * C2S — sent when the player assigns or un-assigns an ability to a hotbar slot
 * from the AbilitiesTabScreen.
 *
 * Payload:
 *   abilityId  (String, max 64 chars) — the ability to assign, or "" to clear
 *   hotbarSlot (int, 0-8)             — the target slot index
 *
 * Server behaviour:
 *   1. Validates the player owns a technique that includes the ability.
 *   2. Saves the assignment to PersistentData NBT.
 *   3. Echoes back a SyncAbilityHotbarS2CPacket with the full updated hotbar.
 */
public class AssignAbilityToHotbarC2SPacket {

    private final String abilityId;  // "" = clear the slot
    private final int    hotbarSlot; // 0-8

    public AssignAbilityToHotbarC2SPacket(String abilityId, int hotbarSlot) {
        this.abilityId  = abilityId  != null ? abilityId : "";
        this.hotbarSlot = hotbarSlot;
    }

    /** Decode constructor. */
    public AssignAbilityToHotbarC2SPacket(FriendlyByteBuf buf) {
        this.abilityId  = buf.readUtf(64); // ← max 64 chars for an ability ID
        this.hotbarSlot = buf.readInt();   // ← slot index (0-8)
    }

    /** Encode. */
    public void toBytes(FriendlyByteBuf buf) {
        buf.writeUtf(abilityId, 64); // ← max 64 chars
        buf.writeInt(hotbarSlot);    // ← slot index
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();
        ctx.enqueueWork(() -> {
            ServerPlayer player = ctx.getSender();
            if (player == null) return;

            // Validate slot range
            if (hotbarSlot < 0 || hotbarSlot >= 9) return;

            CompoundTag data = player.getPersistentData();

            if (abilityId.isEmpty()) {
                // Clear the slot
                data.putString("jjs_hotbar_" + hotbarSlot, "");
            } else {
                // Validate: the ability must belong to the player's current technique
                String techniqueId = data.getString("jjs_technique");
                if (!techniqueId.isEmpty()) {
                    Technique technique = TechniqueRegistry.get(techniqueId);
                    if (technique == null || !technique.getAbilityIds().contains(abilityId)) {
                        // Ability not part of this player's technique — reject silently
                        return;
                    }
                } else {
                    // Player has no technique — reject
                    return;
                }

                // Clear any existing slot that already holds this ability (no duplicates)
                for (int i = 0; i < 9; i++) {
                    if (abilityId.equals(data.getString("jjs_hotbar_" + i))) {
                        data.putString("jjs_hotbar_" + i, "");
                    }
                }

                // Assign
                data.putString("jjs_hotbar_" + hotbarSlot, abilityId);
            }

            // Echo the full updated hotbar back to the client
            String[] slots = new String[9];
            for (int i = 0; i < 9; i++) {
                slots[i] = data.getString("jjs_hotbar_" + i);
            }
            ModNetworking.INSTANCE.send(
                    PacketDistributor.PLAYER.with(() -> player),
                    new SyncAbilityHotbarS2CPacket(slots)
            );
        });
        ctx.setPacketHandled(true);
        return true;
    }
}
