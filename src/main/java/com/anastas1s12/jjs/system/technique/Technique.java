package com.anastas1s12.jjs.system.technique;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.anastas1s12.jjs.system.ability.AbilityRegistry;

/**
 * Represents an Innate Technique — a cursed technique a sorcerer is born with.
 *
 * A technique owns an ordered list of ability IDs. When a technique is assigned
 * to a player those abilities become visible in the Abilities menu and the first
 * N abilities are automatically placed into the ability hotbar slots 0..N-1
 * (up to {@code MAX_HOTBAR_FILL} slots, capped at 9).
 *
 * This is a pure data class — behaviour lives in commands / event handlers.
 *
 * Usage:
 * <pre>
 *   Technique limitless = new Technique.Builder("limitless", "Limitless", ChatFormatting.AQUA)
 *       .ability("divergent_fist")
 *       .ability("black_flash")
 *       .ability("limitless_blue")
 *       .ability("limitless_red")
 *       .build();
 * </pre>
 */
public class Technique {

    /** How many ability IDs are auto-placed into hotbar slots on assignment. */
    public static final int MAX_HOTBAR_FILL = 9;

    private final String id;
    private final String name;
    private final ChatFormatting color;
    private final List<String> abilityIds; // ordered list of ability IDs this technique grants

    private Technique(String id, String name, ChatFormatting color, List<String> abilityIds) {
        this.id        = id;
        this.name      = name;
        this.color     = color;
        this.abilityIds = Collections.unmodifiableList(new ArrayList<>(abilityIds));
    }

    // ---- Getters ------------------------------------------------------------

    /** Unique string ID, e.g. {@code "limitless"}. */
    public String getId() { return id; }

    /** Display name, e.g. {@code "Limitless"}. */
    public String getName() { return name; }

    /** Chat colour used when displaying this technique's name. */
    public ChatFormatting getColor() { return color; }

    /** Ordered list of ability IDs granted by this technique. */
    public List<String> getAbilityIds() { return abilityIds; }

    /**
     * Returns a {@link Component} of the technique name in its display colour.
     */
    public Component getDisplayComponent() {
        return Component.literal(name).withStyle(color);
    }

    /**
     * Builds an array of exactly 9 hotbar slot strings for this technique.
     * The first {@code min(abilityIds.size(), 9)} slots are filled with
     * the technique's ability IDs in order; remaining slots are {@code ""}.
     *
     * @return String[9] ready to pass to SyncAbilityHotbarS2CPacket.
     */
    public String[] buildDefaultHotbar() {
        String[] slots = new String[MAX_HOTBAR_FILL];
        for (int i = 0; i < MAX_HOTBAR_FILL; i++) {
            slots[i] = (i < abilityIds.size()) ? abilityIds.get(i) : "";
        }
        return slots;
    }

    // =========================================================================
    // Builder
    // =========================================================================

    public static class Builder {
        private final String id;
        private final String name;
        private final ChatFormatting color;
        private final List<String> abilityIds = new ArrayList<>();

        public Builder(String id, String name, ChatFormatting color) {
            this.id    = id;
            this.name  = name;
            this.color = color;
        }

        /**
         * Add an ability ID to this technique's ability list.
         * The ID must be registered in {@link AbilityRegistry} before use.
         */
        public Builder ability(String abilityId) {
            abilityIds.add(abilityId);
            return this;
        }

        public Technique build() {
            return new Technique(id, name, color, abilityIds);
        }
    }
}
