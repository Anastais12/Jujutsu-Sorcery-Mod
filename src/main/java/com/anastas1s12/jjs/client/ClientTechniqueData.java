package com.anastas1s12.jjs.client;

import com.anastas1s12.jjs.system.ability.Ability;
import com.anastas1s12.jjs.system.ability.AbilityRegistry;
import com.anastas1s12.jjs.system.technique.Technique;
import com.anastas1s12.jjs.system.technique.TechniqueRegistry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Client-side cache of the player's currently assigned technique.
 *
 * Updated by {@link com.anastas1s12.jjs.networking.s2c.SyncTechniqueS2CPacket}.
 *
 * The Abilities menu and hotbar overlay read from here to know which abilities
 * to display. When the technique changes the ability list changes automatically
 * because it is derived on the fly from the registered {@link Technique} data.
 */
public final class ClientTechniqueData {

    private ClientTechniqueData() {}

    /** Currently assigned technique ID, or {@code ""} if none. */
    private static String techniqueId = "";

    // =========================================================================
    // Update from server
    // =========================================================================

    /**
     * Called by {@code SyncTechniqueS2CPacket} to update the local cache.
     *
     * @param id The technique ID received from the server (may be empty).
     */
    public static void setTechnique(String id) {
        techniqueId = (id != null) ? id : "";
    }

    // =========================================================================
    // Getters
    // =========================================================================

    /**
     * Returns the current technique ID, or {@code ""} if none is assigned.
     */
    public static String getTechniqueId() {
        return techniqueId;
    }

    /**
     * Returns the current {@link Technique} resolved from {@link TechniqueRegistry},
     * or {@code null} if none is assigned or the ID is not registered.
     */
    public static Technique getTechnique() {
        if (techniqueId.isEmpty()) return null;
        return TechniqueRegistry.get(techniqueId);
    }

    /**
     * Returns true if the player currently has a technique assigned.
     */
    public static boolean hasTechnique() {
        return !techniqueId.isEmpty() && TechniqueRegistry.contains(techniqueId);
    }

    /**
     * Returns all {@link Ability} objects granted by the current technique,
     * in the order the technique defines them.
     *
     * Returns an empty list if no technique is assigned or the ability IDs
     * are not registered.
     */
    public static List<Ability> getTechniqueAbilities() {
        Technique technique = getTechnique();
        if (technique == null) return Collections.emptyList();

        List<Ability> abilities = new ArrayList<>();
        for (String id : technique.getAbilityIds()) {
            Ability ability = AbilityRegistry.get(id);
            if (ability != null) abilities.add(ability);
        }
        return abilities;
    }

    /**
     * Returns the display name of the current technique, or {@code "None"}.
     */
    public static String getTechniqueName() {
        Technique t = getTechnique();
        return (t != null) ? t.getName() : "None";
    }
}
