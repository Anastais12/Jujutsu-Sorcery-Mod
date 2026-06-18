package com.anastas1s12.jjs.client;

/**
 * Client-side singleton that tracks sorcerer mode state and the currently
 * selected ability hotbar slot.
 *
 * Sorcerer Mode:
 *   - Toggled by pressing R (replaces the old RCT toggle on that key).
 *   - When active: the vanilla hotbar is hidden and the sorcerer ability
 *     hotbar overlay is rendered instead.
 *   - The server is notified via ToggleSorcererModeC2SPacket and confirms
 *     the new state back via SorcererModeSyncS2CPacket.
 *
 * Selected slot:
 *   - 0-based index into the 9-slot ability hotbar.
 *   - Scrolled with the mouse wheel (same as vanilla hotbar scroll) while
 *     sorcerer mode is active.
 *   - Defaults to slot 0.
 */
public final class ClientSorcererState {

    private ClientSorcererState() {}

    // ---- State fields -------------------------------------------------------

    /** Whether sorcerer mode is currently active on the client. */
    private static boolean sorcererModeActive = false;

    /** Currently selected slot (0-8). */
    private static int selectedSlot = 0;

    // ---- Sorcerer mode ------------------------------------------------------

    public static boolean isSorcererModeActive() {
        return sorcererModeActive;
    }

    /**
     * Called by SorcererModeSyncS2CPacket when the server confirms a mode change.
     */
    public static void setSorcererModeActive(boolean active) {
        sorcererModeActive = active;
        if (!active) selectedSlot = 0; // reset selection when exiting
    }

    // ---- Slot selection -----------------------------------------------------

    public static int getSelectedSlot() {
        return selectedSlot;
    }

    /**
     * Scroll the selected slot by {@code delta} steps.
     * Wraps around at 0 and {@code HOTBAR_SLOTS - 1}.
     */
    public static void scrollSlot(int delta) {
        selectedSlot = Math.floorMod(selectedSlot + delta, ClientAbilityData.HOTBAR_SLOTS);
    }

    /** Directly set the selected slot (clamped to valid range). */
    public static void setSelectedSlot(int slot) {
        selectedSlot = Math.max(0, Math.min(slot, ClientAbilityData.HOTBAR_SLOTS - 1));
    }
}
