package com.anastas1s12.jjs.ability;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Central registry of every ability in the mod.
 *
 * All abilities from every technique are registered here so they can be
 * looked up by ID from both the client (UI rendering) and the server
 * (validation, execution).
 *
 * To add an ability:
 *   1. Add a new {@link Ability.Builder} block inside {@link #init()}.
 *   2. Reference its ID in the matching {@link TechniqueRegistry} entry.
 */
public final class AbilityRegistry {

    private AbilityRegistry() {}

    private static final Map<String, Ability> REGISTRY = new LinkedHashMap<>();

    // =========================================================================
    // Init
    // =========================================================================

    public static void init() {

        // ─────────────────────────────────────────────────────────────────────
        // UNIVERSAL BASICS
        // Available to every technique — fundamental CE combat applications.
        // ─────────────────────────────────────────────────────────────────────

        register(new Ability.Builder("divergent_fist", "Divergent Fist", AbilityType.BASIC)
                .ceCost(15f).cooldown(0)
                .damageType(DamageType.PHYSICAL)
                .description("Releases CE the instant a strike")
                .description("connects, creating a delayed second")
                .description("shockwave that bypasses defences.")
                .requirement("Mastery Level 0")
                .build());

        register(new Ability.Builder("black_flash", "Black Flash", AbilityType.BASIC)
                .ceCost(30f).cooldown(5)
                .damageType(DamageType.CURSED)
                .description("A CE strike aligned with the exact")
                .description("moment of physical impact, warping")
                .description("space and amplifying damage 2.5×.")
                .requirement("Mastery Level 5")
                .build());

        register(new Ability.Builder("ce_reinforcement", "CE Reinforcement", AbilityType.BASIC)
                .ceCost(10f).cooldown(0)
                .damageType(DamageType.PHYSICAL)
                .description("Coats the body in cursed energy,")
                .description("boosting movement speed and damage")
                .description("for a short burst.")
                .requirement("Mastery Level 0")
                .build());

        // ─────────────────────────────────────────────────────────────────────
        // LIMITLESS TECHNIQUE  (Gojo Satoru)
        // ─────────────────────────────────────────────────────────────────────

        register(new Ability.Builder("limitless_blue", "Limitless: Blue", AbilityType.ADVANCED)
                .ceCost(80f).cooldown(8)
                .damageType(DamageType.SPATIAL)
                .description("Inverts the natural flow of CE to")
                .description("create an imaginary convergence")
                .description("point. Pulls enemies and objects")
                .description("toward a focal point with crushing")
                .description("gravitational force.")
                .requirement("Mastery Level 15")
                .build());

        register(new Ability.Builder("limitless_red", "Limitless: Red", AbilityType.ADVANCED)
                .ceCost(120f).cooldown(12)
                .damageType(DamageType.SPATIAL)
                .description("Amplifies divergence to its extreme,")
                .description("generating an overwhelmingly repulsive")
                .description("force that blasts everything nearby")
                .description("outward at devastating speed.")
                .requirement("Mastery Level 25")
                .build());

        register(new Ability.Builder("infinity", "Infinity", AbilityType.ADVANCED)
                .ceCost(0f).cooldown(0)
                .damageType(DamageType.SPATIAL)
                .description("A passive barrier of infinite")
                .description("sub-divisions that slows anything")
                .description("approaching to a halt. Costs CE")
                .description("per second while active.")
                .requirement("Mastery Level 30")
                .build());

        register(new Ability.Builder("hollow_purple", "Hollow Purple", AbilityType.SPECIAL_MOVE)
                .ceCost(300f).cooldown(45)
                .damageType(DamageType.VOID)
                .description("Combines Blue and Red into a single")
                .description("invisible sphere of imaginary mass.")
                .description("Erases everything in its path from")
                .description("existence — there is no defence.")
                .requirement("Mastery Level 50")
                .build());

        register(new Ability.Builder("unlimited_void", "Unlimited Void", AbilityType.DOMAIN)
                .ceCost(500f).cooldown(180)
                .damageType(DamageType.VOID)
                .description("Domain Expansion: Infinite Void.")
                .description("Traps all targets in a boundless")
                .description("stream of information, stimulus,")
                .description("and sensation. The mind cannot")
                .description("process commands — guaranteed hit.")
                .requirement("Mastery Level 100")
                .build());

        // ─────────────────────────────────────────────────────────────────────
        // TEN SHADOWS TECHNIQUE  (Megumi Fushiguro)
        // ─────────────────────────────────────────────────────────────────────

        register(new Ability.Builder("shadow_well", "Shadow Well", AbilityType.BASIC)
                .ceCost(20f).cooldown(3)
                .damageType(DamageType.CURSED)
                .description("Dives into the user's own shadow,")
                .description("emerging from any shadow within")
                .description("a 10-block radius.")
                .requirement("Mastery Level 0")
                .build());

        register(new Ability.Builder("divine_dogs", "Divine Dogs", AbilityType.ADVANCED)
                .ceCost(60f).cooldown(15)
                .damageType(DamageType.SLASHING)
                .description("Summons two divine dog shikigami")
                .description("that track and assault the target.")
                .description("Each dog deals independent damage.")
                .requirement("Mastery Level 10")
                .build());

        register(new Ability.Builder("nue", "Nue", AbilityType.ADVANCED)
                .ceCost(75f).cooldown(18)
                .damageType(DamageType.EXPLOSION)
                .description("Summons Nue, a thunder-bird")
                .description("shikigami that dive-bombs the")
                .description("target dealing lightning damage")
                .description("over a wide area.")
                .requirement("Mastery Level 20")
                .build());

        register(new Ability.Builder("toad", "Toad", AbilityType.ADVANCED)
                .ceCost(55f).cooldown(12)
                .damageType(DamageType.PHYSICAL)
                .description("Summons a giant toad that")
                .description("restrains the target with its")
                .description("sticky tongue, holding them in")
                .description("place for several seconds.")
                .requirement("Mastery Level 15")
                .build());

        register(new Ability.Builder("max_elephant", "Max Elephant", AbilityType.SPECIAL_MOVE)
                .ceCost(200f).cooldown(40)
                .damageType(DamageType.BLUNT)
                .description("Summons Max Elephant, a colossal")
                .description("shikigami that unleashes a torrent")
                .description("of water and tramples all enemies")
                .description("in its path.")
                .requirement("Mastery Level 45")
                .build());

        register(new Ability.Builder("chimera_shadow_garden", "Chimera Shadow Garden", AbilityType.DOMAIN)
                .ceCost(500f).cooldown(180)
                .damageType(DamageType.CURSED)
                .description("Domain Expansion: Chimera Shadow")
                .description("Garden. Floods the area in liquid")
                .description("shadow. All shikigami are enhanced")
                .description("and the user can summon multiple")
                .description("at no extra cost — guaranteed hit.")
                .requirement("Mastery Level 100")
                .build());

        // ─────────────────────────────────────────────────────────────────────
        // BLOOD MANIPULATION  (Choso / Kamo clan)
        // ─────────────────────────────────────────────────────────────────────

        register(new Ability.Builder("piercing_blood", "Piercing Blood", AbilityType.ADVANCED)
                .ceCost(70f).cooldown(6)
                .damageType(DamageType.PIERCING)
                .description("Fires a hyper-compressed stream of")
                .description("blood at supersonic speed. Near")
                .description("impossible to dodge at close range.")
                .requirement("Mastery Level 15")
                .build());

        register(new Ability.Builder("convergence", "Convergence", AbilityType.BASIC)
                .ceCost(25f).cooldown(4)
                .damageType(DamageType.PIERCING)
                .description("Compresses blood into a dense ball,")
                .description("then launches it as a devastating")
                .description("projectile.")
                .requirement("Mastery Level 5")
                .build());

        register(new Ability.Builder("supernova", "Supernova", AbilityType.SPECIAL_MOVE)
                .ceCost(250f).cooldown(60)
                .damageType(DamageType.EXPLOSION)
                .description("Combines Convergence and Piercing")
                .description("Blood into a singular explosive")
                .description("blast that detonates on contact,")
                .description("shredding everything in range.")
                .requirement("Mastery Level 55")
                .build());

        register(new Ability.Builder("blood_meteorite", "Blood Meteorite", AbilityType.SPECIAL_MOVE)
                .ceCost(200f).cooldown(50)
                .damageType(DamageType.EXPLOSION)
                .description("Launches a massive sphere of")
                .description("condensed blood skyward, then")
                .description("detonates it as a falling meteor.")
                .requirement("Mastery Level 40")
                .build());

        // ─────────────────────────────────────────────────────────────────────
        // IDLE TRANSFIGURATION  (Mahito)
        // ─────────────────────────────────────────────────────────────────────

        register(new Ability.Builder("soul_distortion", "Soul Distortion", AbilityType.BASIC)
                .ceCost(20f).cooldown(2)
                .damageType(DamageType.TRUE)
                .description("Touches the target's soul directly,")
                .description("bypassing the body entirely. Deals")
                .description("true damage that ignores all CE")
                .description("reinforcement and armour.")
                .requirement("Mastery Level 0")
                .build());

        register(new Ability.Builder("polymorphic_soul_isomer", "Polymorphic Soul Isomer", AbilityType.ADVANCED)
                .ceCost(100f).cooldown(20)
                .damageType(DamageType.TRUE)
                .description("Rapidly reshapes the user's own")
                .description("body during combat, creating extra")
                .description("limbs or a hardened shell to block")
                .description("an incoming attack.")
                .requirement("Mastery Level 20")
                .build());

        register(new Ability.Builder("body_repel", "Body Repel", AbilityType.ADVANCED)
                .ceCost(90f).cooldown(16)
                .damageType(DamageType.TRUE)
                .description("Compresses and releases the target's")
                .description("soul structure, violently ejecting")
                .description("them and dealing true damage.")
                .requirement("Mastery Level 25")
                .build());

        register(new Ability.Builder("self_embodiment_of_perfection",
                "Self Embodiment of Perfection", AbilityType.DOMAIN)
                .ceCost(500f).cooldown(180)
                .damageType(DamageType.TRUE)
                .description("Domain Expansion: Self Embodiment")
                .description("of Perfection. Every surface is")
                .description("coated with Mahito's soul, meaning")
                .description("all contact deals true soul damage")
                .description("— guaranteed hit.")
                .requirement("Mastery Level 100")
                .build());
    }

    // =========================================================================
    // API
    // =========================================================================

    public static void register(Ability ability) {
        if (REGISTRY.containsKey(ability.getId())) {
            throw new IllegalStateException("Duplicate ability ID: " + ability.getId());
        }
        REGISTRY.put(ability.getId(), ability);
    }

    /** @return The ability, or {@code null} if not registered. */
    public static Ability get(String id) {
        return REGISTRY.get(id);
    }

    public static Collection<Ability> getAll() {
        return Collections.unmodifiableCollection(REGISTRY.values());
    }

    public static boolean contains(String id) {
        return REGISTRY.containsKey(id);
    }
}
