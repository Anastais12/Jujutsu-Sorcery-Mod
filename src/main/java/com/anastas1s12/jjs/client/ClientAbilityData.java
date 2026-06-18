package com.anastas1s12.jjs.client;

import com.anastas1s12.jjs.system.ability.Ability;
import com.anastas1s12.jjs.system.ability.AbilityRegistry;

import java.util.Arrays;

public final class ClientAbilityData {

    private ClientAbilityData() {}

    public static final int HOTBAR_SLOTS = 9;

    /** Ability IDs assigned to each hotbar slot. "" = empty. */
    private static final String[] slotIds = new String[HOTBAR_SLOTS];

    static {
        Arrays.fill(slotIds, "");
    }

    /**
     * Called by SyncAbilityHotbarS2CPacket to update the full hotbar.
     *
     * @param ids Array of HOTBAR_SLOTS ability ID strings. "" = empty slot.
     */
    public static void setHotbar(String[] ids) {
        for (int i = 0; i < HOTBAR_SLOTS; i++) {
            slotIds[i] = (ids != null && i < ids.length && ids[i] != null) ? ids[i] : "";
        }
    }

    /**
     * Returns the ability ID in slot {@code slot}, or "" if empty.
     */
    public static String getSlotId(int slot) {
        if (slot < 0 || slot >= HOTBAR_SLOTS) return "";
        return slotIds[slot];
    }

    /**
     * Resolves the ability in slot {@code slot} from the AbilityRegistry.
     * Returns null if the slot is empty or the ID is not registered.
     */
    public static Ability getSlotAbility(int slot) {
        String id = getSlotId(slot);
        if (id.isEmpty()) return null;
        return AbilityRegistry.get(id);
    }

    /**
     * Returns true if slot {@code slot} has an ability assigned.
     */
    public static boolean isSlotFilled(int slot) {
        return !getSlotId(slot).isEmpty();
    }

    /**
     * Optimistically updates a single slot on the client before the server
     * echo arrives. Called by AbilitiesTabScreen immediately after sending
     * AssignAbilityToHotbarC2SPacket so the UI feels instant.
     *
     * @param slot     The slot index (0-8).
     * @param abilityId The ability ID to set, or "" to clear.
     */
    public static void setSlotOptimistic(int slot, String abilityId) {
        if (slot < 0 || slot >= HOTBAR_SLOTS) return;
        String id = (abilityId != null) ? abilityId : "";

        if (!id.isEmpty()) {
            for (int i = 0; i < HOTBAR_SLOTS; i++) {
                if (id.equals(slotIds[i])) slotIds[i] = "";
            }
        }
        slotIds[slot] = id;
    }
}
