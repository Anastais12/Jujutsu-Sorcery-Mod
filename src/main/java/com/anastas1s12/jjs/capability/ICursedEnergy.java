package com.anastas1s12.jjs.capability;

import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.util.INBTSerializable;

/**
 * Core interface for the Cursed Energy capability.
 * This defines all operations for managing a player's Cursed Energy resource,
 * including current/max CE, regeneration, efficiency, output, and permanent reserves.
 */
public interface ICursedEnergy extends INBTSerializable<CompoundTag> {

    // ============================================================
    // Core CE Stats
    // ============================================================

    /** Current CE amount (float for precision). */
    float getCurrentCE();

    void setCurrentCE(float amount);

    /** Maximum CE cap. This is baseMaxCE + all bonuses. */
    float getMaxCE();

    /** The permanent base max CE (grown through training/items). */
    float getBaseMaxCE();

    void setBaseMaxCE(float amount);

    /** Recalculate maxCE from base + active bonuses. Call after base changes. */
    void recalculateMaxCE();

    // ============================================================
    // CE Regeneration
    // ============================================================

    /** CE regenerated per tick. Base is very small (e.g., 0.05f = 1 CE/sec at 20 tps). */
    float getRegenRate();

    void setRegenRate(float rate);

    /** Base regen rate before modifiers. */
    float getBaseRegenRate();

    void setBaseRegenRate(float rate);

    /** Active regeneration multiplier from buffs/debuffs. 1.0 = normal. */
    float getRegenMultiplier();

    void setRegenMultiplier(float multiplier);

    /** Whether regeneration is currently paused (e.g., during Domain Expansion). */
    boolean isRegenPaused();

    void setRegenPaused(boolean paused);

    // ============================================================
    // CE Efficiency (cost reduction)
    // ============================================================

    /**
     * Efficiency as a decimal 0.0 to 1.0+.
     * 0.0 = no reduction, 0.25 = 25% reduction, 0.5 = 50% reduction, etc.
     * Can exceed 1.0 for free abilities (though capped in practice).
     */
    float getEfficiency();

    void setEfficiency(float efficiency);

    /** Base efficiency before modifiers. */
    float getBaseEfficiency();

    void setBaseEfficiency(float efficiency);

    /**
     * Calculates the actual CE cost after efficiency is applied.
     * cost = baseCost * (1.0 - efficiency), clamped to minimum of 1.0.
     */
    float getEffectiveCost(float baseCost);

    // ============================================================
    // CE Output (damage potency)
    // ============================================================

    /**
     * Output multiplier for damage calculations.
     * 1.0 = normal damage, 2.0 = double damage, etc.
     */
    float getOutput();

    void setOutput(float output);

    /** Base output before modifiers. */
    float getBaseOutput();

    void setBaseOutput(float output);

    /**
     * Calculates effective damage after applying CE output.
     * damage = baseDamage * output.
     */
    float getEffectiveDamage(float baseDamage);

    // ============================================================
    // Consumption & Regeneration Operations
    // ============================================================

    /**
     * Attempts to consume the given base amount of CE.
     * Applies efficiency modifier automatically.
     *
     * @param baseCost The base CE cost before efficiency.
     * @return true if the player had enough CE and it was consumed.
     */
    boolean consume(float baseCost);

    /**
     * Forces consumption even if it would go negative.
     * Use for abilities that can overdraw (with consequences).
     */
    void forceConsume(float baseCost);

    /** Adds CE, clamped to maxCE. */
    void add(float amount);

    /** Called every player tick to handle passive regeneration. */
    void onTick();

    // ============================================================
    // State Flags
    // ============================================================

    /** Whether the player is currently channeling an ability (prevents regen or other effects). */
    boolean isChanneling();

    void setChanneling(boolean channeling);

    /** Whether Reverse Cursed Technique is active. */
    boolean isRCTActive();

    void setRCTActive(boolean active);

    /** Whether the player has Six Eyes unlocked. */
    boolean hasSixEyes();

    void setSixEyes(boolean has);

    // ============================================================
    // Mastery (for skill tree progression)
    // ============================================================

    int getMasteryPoints();

    void setMasteryPoints(int points);

    void addMasteryPoints(int points);

    /** Total mastery level (0-100+). Gates abilities at 50 and 100. */
    int getMasteryLevel();

    void setMasteryLevel(int level);

    /** Add mastery XP. Handles leveling up automatically. */
    void addMasteryXP(int xp);

    int getMasteryXP();

    // ============================================================
    // Sukuna Fingers Consumed
    // ============================================================

    int getFingersConsumed();

    void setFingersConsumed(int count);

    void addFinger();

    // ============================================================
    // Utility
    // ============================================================

    /** Returns percentage of current CE (0.0 to 1.0). */
    default float getCERatio() {
        if (getMaxCE() <= 0) return 0.0f;
        return getCurrentCE() / getMaxCE();
    }

    /** Whether current CE is above the given percentage of max. */
    default boolean isAbovePercent(float percent) {
        return getCERatio() >= percent;
    }

    /** Copy data from another ICursedEnergy instance. Used on player respawn/clone. */
    void copyFrom(ICursedEnergy source);
}
