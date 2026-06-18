package com.anastas1s12.jjs.system.technique;

import net.minecraft.ChatFormatting;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import com.anastas1s12.jjs.system.ability.AbilityRegistry;

/**
 * Central registry for every {@link Technique} in the mod.
 *
 * Call {@link #init()} once from FMLCommonSetupEvent, AFTER
 * {@link AbilityRegistry#init()} so all ability IDs are already valid.
 *
 * To add a technique:
 *   1. Register its abilities in {@link AbilityRegistry#init()} first.
 *   2. Build it with {@link Technique.Builder} here, listing ability IDs
 *      in the order they should fill the hotbar (slot 0, 1, 2 …).
 */
public final class TechniqueRegistry {

    private TechniqueRegistry() {}

    private static final Map<String, Technique> REGISTRY = new LinkedHashMap<>();

    // =========================================================================
    // Init
    // =========================================================================

    public static void init() {

        // ── Limitless (Gojo Satoru) ──────────────────────────────────────────
        // Spatial manipulation technique. Manipulates infinity itself.
        register(new Technique.Builder("limitless", "Limitless", ChatFormatting.AQUA)
                .ability("ce_reinforcement")   // slot 0 — baseline CE combat
                .ability("divergent_fist")      // slot 1 — basic CE strike
                .ability("black_flash")         // slot 2 — amplified CE strike
                .ability("limitless_blue")      // slot 3 — attraction singularity
                .ability("limitless_red")       // slot 4 — repulsion blast
                .ability("infinity")            // slot 5 — passive barrier
                .ability("hollow_purple")       // slot 6 — combined void sphere
                .ability("unlimited_void")      // slot 7 — domain expansion
                .build());

        // ── Ten Shadows (Megumi Fushiguro) ───────────────────────────────────
        // Summons shikigami through shadows using ten sacred hand-signs.
        register(new Technique.Builder("ten_shadows", "Ten Shadows", ChatFormatting.DARK_GRAY)
                .ability("ce_reinforcement")    // slot 0 — baseline CE combat
                .ability("divergent_fist")      // slot 1 — basic CE strike
                .ability("shadow_well")         // slot 2 — shadow travel
                .ability("divine_dogs")         // slot 3 — tracking shikigami
                .ability("toad")                // slot 4 — restraint shikigami
                .ability("nue")                 // slot 5 — aerial shikigami
                .ability("max_elephant")        // slot 6 — heavy shikigami
                .ability("chimera_shadow_garden") // slot 7 — domain expansion
                .build());

        // ── Blood Manipulation (Choso / Kamo clan) ───────────────────────────
        // Controls blood as a weapon — both the user's own and others'.
        register(new Technique.Builder("blood_manipulation", "Blood Manipulation", ChatFormatting.RED)
                .ability("ce_reinforcement")    // slot 0 — baseline CE combat
                .ability("divergent_fist")      // slot 1 — basic CE strike
                .ability("black_flash")         // slot 2 — amplified CE strike
                .ability("convergence")         // slot 3 — compressed blood shot
                .ability("piercing_blood")      // slot 4 — supersonic blood stream
                .ability("blood_meteorite")     // slot 5 — falling blood meteor
                .ability("supernova")           // slot 6 — explosive combo
                .build());

        // ── Idle Transfiguration (Mahito) ────────────────────────────────────
        // Reshapes souls directly — bypasses all physical defences.
        register(new Technique.Builder("idle_transfiguration", "Idle Transfiguration", ChatFormatting.LIGHT_PURPLE)
                .ability("ce_reinforcement")            // slot 0 — baseline CE combat
                .ability("divergent_fist")               // slot 1 — basic CE strike
                .ability("soul_distortion")              // slot 2 — true-damage touch
                .ability("polymorphic_soul_isomer")      // slot 3 — body reshape
                .ability("body_repel")                   // slot 4 — soul ejection
                .ability("self_embodiment_of_perfection") // slot 5 — domain expansion
                .build());
    }

    // =========================================================================
    // API
    // =========================================================================

    public static void register(Technique technique) {
        if (REGISTRY.containsKey(technique.getId())) {
            throw new IllegalStateException("Duplicate technique ID: " + technique.getId());
        }
        REGISTRY.put(technique.getId(), technique);
    }

    /** @return The technique, or {@code null} if not registered. */
    public static Technique get(String id) {
        return REGISTRY.get(id);
    }

    public static Collection<Technique> getAll() {
        return Collections.unmodifiableCollection(REGISTRY.values());
    }

    public static Collection<String> getIds() {
        return Collections.unmodifiableSet(REGISTRY.keySet());
    }

    public static boolean contains(String id) {
        return REGISTRY.containsKey(id);
    }
}
