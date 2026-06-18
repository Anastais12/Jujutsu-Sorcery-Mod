package com.anastas1s12.jjs.ability;

import com.anastas1s12.jjs.networking.ModNetworking;
import com.anastas1s12.jjs.networking.s2c.SyncAbilityHotbarS2CPacket;
import com.anastas1s12.jjs.networking.s2c.SyncTechniqueS2CPacket;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.PacketDistributor;

/**
 * Server-side helper for reading and writing technique + hotbar data stored
 * in a player's {@link net.minecraft.world.entity.player.Player#getPersistentData()}.
 *
 * NBT keys used (all under the player's persistent-data CompoundTag):
 *   "jjs_technique"   — String, the assigned technique ID (empty = none)
 *   "jjs_hotbar_0" … "jjs_hotbar_8" — Strings, ability IDs in each of the
 *                                       9 sorcerer-hotbar slots (empty = unoccupied)
 *
 * Main entry-point: {@link #assignTechnique(ServerPlayer, Technique)}
 * Call this from the command handler whenever a technique is assigned.
 */
public final class PlayerTechniqueData {

    private PlayerTechniqueData() {}

    // NBT key for the technique ID
    private static final String KEY_TECHNIQUE = "jjs_technique";
    // NBT key prefix for hotbar slot IDs
    private static final String KEY_HOTBAR_PREFIX = "jjs_hotbar_";

    // =========================================================================
    // Assign technique to a player
    // =========================================================================

    /**
     * Assigns {@code technique} to {@code player}:
     *   1. Saves the technique ID to PersistentData NBT.
     *   2. Builds the default 9-slot hotbar from the technique's ability list
     *      and saves each slot to PersistentData.
     *   3. Sends {@link SyncTechniqueS2CPacket} so the client knows the new technique.
     *   4. Sends {@link SyncAbilityHotbarS2CPacket} so the client knows the new hotbar.
     *
     * @param player    The target player (server-side).
     * @param technique The technique to assign. Pass {@code null} to clear.
     */
    public static void assignTechnique(ServerPlayer player, Technique technique) {
        CompoundTag data = player.getPersistentData();

        if (technique == null) {
            // Clear technique and hotbar
            data.putString(KEY_TECHNIQUE, "");
            for (int i = 0; i < 9; i++) {
                data.putString(KEY_HOTBAR_PREFIX + i, "");
            }
            sendSync(player, "", new String[9]);
            return;
        }

        // Save technique ID
        data.putString(KEY_TECHNIQUE, technique.getId());

        // Build and save default hotbar
        String[] slots = technique.buildDefaultHotbar();
        for (int i = 0; i < 9; i++) {
            data.putString(KEY_HOTBAR_PREFIX + i, slots[i] != null ? slots[i] : "");
        }

        // Sync to client
        sendSync(player, technique.getId(), slots);
    }

    // =========================================================================
    // Load and sync on login / respawn / dimension change
    // =========================================================================

    /**
     * Reads the stored technique and hotbar from PersistentData and syncs both
     * packets to the client.  Call this from login / respawn / dimension-change
     * event handlers so the client is always up to date.
     *
     * @param player The player to sync (server-side).
     */
    public static void syncToClient(ServerPlayer player) {
        CompoundTag data = player.getPersistentData();

        String techniqueId = data.getString(KEY_TECHNIQUE); // "" if missing

        String[] slots = new String[9];
        for (int i = 0; i < 9; i++) {
            slots[i] = data.getString(KEY_HOTBAR_PREFIX + i); // "" if missing
        }

        sendSync(player, techniqueId, slots);
    }

    // =========================================================================
    // Getters
    // =========================================================================

    /**
     * Returns the technique currently assigned to {@code player},
     * or {@code null} if none is assigned.
     */
    public static Technique getTechnique(ServerPlayer player) {
        String id = player.getPersistentData().getString(KEY_TECHNIQUE);
        if (id.isEmpty()) return null;
        return TechniqueRegistry.get(id);
    }

    /**
     * Returns the technique ID string stored in PersistentData, or {@code ""}
     * if the player has no technique.
     */
    public static String getTechniqueId(ServerPlayer player) {
        return player.getPersistentData().getString(KEY_TECHNIQUE);
    }

    /**
     * Returns the array of 9 hotbar slot IDs stored in PersistentData.
     * Each element is an ability ID string or {@code ""} for an empty slot.
     */
    public static String[] getHotbarSlots(ServerPlayer player) {
        CompoundTag data = player.getPersistentData();
        String[] slots = new String[9];
        for (int i = 0; i < 9; i++) {
            slots[i] = data.getString(KEY_HOTBAR_PREFIX + i);
        }
        return slots;
    }

    // =========================================================================
    // Private helpers
    // =========================================================================

    private static void sendSync(ServerPlayer player, String techniqueId, String[] slots) {
        // Sync technique ID + ability list
        ModNetworking.INSTANCE.send(
                PacketDistributor.PLAYER.with(() -> player),
                new SyncTechniqueS2CPacket(techniqueId)
        );

        // Sync hotbar slot assignments
        ModNetworking.INSTANCE.send(
                PacketDistributor.PLAYER.with(() -> player),
                new SyncAbilityHotbarS2CPacket(slots)
        );
    }
}
