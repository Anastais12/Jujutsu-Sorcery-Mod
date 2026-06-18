package com.anastas1s12.jjs.capability;

import net.minecraft.nbt.CompoundTag;

public class CursedEnergy implements ICursedEnergy {

    public static final float DEFAULT_BASE_MAX_CE = 1000.0f;
    public static final float DEFAULT_BASE_REGEN = 0.10f; // 1 CE per second at 20 tps
    public static final float DEFAULT_BASE_EFFICIENCY = 0.0f;
    public static final float DEFAULT_BASE_OUTPUT = 1.0f;

    public static final int MASTERY_RCT_UNLOCK = 50;
    public static final int MASTERY_SPECIAL_GRADE = 100;
    public static final int MASTERY_XP_PER_LEVEL = 100; // XP needed per mastery level

    private float currentCE = DEFAULT_BASE_MAX_CE;
    private float baseMaxCE = DEFAULT_BASE_MAX_CE;
    private float maxCE = DEFAULT_BASE_MAX_CE;

    private float baseRegenRate = DEFAULT_BASE_REGEN;
    private float regenMultiplier = 1.0f;
    private boolean regenPaused = false;

    private float baseEfficiency = DEFAULT_BASE_EFFICIENCY;
    private float efficiency = DEFAULT_BASE_EFFICIENCY;

    private float baseOutput = DEFAULT_BASE_OUTPUT;
    private float output = DEFAULT_BASE_OUTPUT;

    private boolean channeling = false;
    private boolean rctActive = false;
    private boolean sixEyes = false;

    private int masteryPoints = 0;
    private int masteryLevel = 0;
    private int masteryXP = 0;

    private int fingersConsumed = 0;

    // Getters & Setters
    @Override
    public float getCurrentCE() {
        return this.currentCE;
    }

    @Override
    public void setCurrentCE(float amount) {
        this.currentCE = Math.max(0.0f, Math.min(amount, this.maxCE));
    }

    @Override
    public float getMaxCE() {
        return this.maxCE;
    }

    @Override
    public float getBaseMaxCE() {
        return this.baseMaxCE;
    }

    @Override
    public void setBaseMaxCE(float amount) {
        this.baseMaxCE = Math.max(50.0f, amount); // Hard floor of 50
        recalculateMaxCE();
    }

    @Override
    public void recalculateMaxCE() {
        if (this.currentCE > this.maxCE) {
            this.currentCE = this.maxCE;
        }
    }

    @Override
    public float getRegenRate() {
        if (regenPaused || rctActive || channeling) return 0.0f;
        return this.baseRegenRate * this.regenMultiplier;
    }

    @Override
    public void setRegenRate(float rate) {
        // This sets the base rate
        this.baseRegenRate = rate;
    }

    @Override
    public float getBaseRegenRate() {
        return this.baseRegenRate;
    }

    @Override
    public void setBaseRegenRate(float rate) {
        this.baseRegenRate = Math.max(0.0f, rate);
    }

    @Override
    public float getRegenMultiplier() {
        return this.regenMultiplier;
    }

    @Override
    public void setRegenMultiplier(float multiplier) {
        this.regenMultiplier = Math.max(0.0f, multiplier);
    }

    @Override
    public boolean isRegenPaused() {
        return this.regenPaused;
    }

    @Override
    public void setRegenPaused(boolean paused) {
        this.regenPaused = paused;
    }

    @Override
    public float getEfficiency() {
        return this.efficiency;
    }

    @Override
    public void setEfficiency(float efficiency) {
        this.efficiency = Math.max(0.0f, Math.min(0.95f, efficiency)); // Cap at 95%
    }

    @Override
    public float getBaseEfficiency() {
        return this.baseEfficiency;
    }

    @Override
    public void setBaseEfficiency(float efficiency) {
        this.baseEfficiency = Math.max(0.0f, Math.min(0.95f, efficiency));
        this.efficiency = this.baseEfficiency; // Recalculate total
    }

    @Override
    public float getEffectiveCost(float baseCost) {
        float cost = baseCost * (1.0f - this.efficiency);
        return Math.max(1.0f, cost); // Minimum cost of 1 CE
    }

    @Override
    public float getOutput() {
        return this.output;
    }

    @Override
    public void setOutput(float output) {
        this.output = Math.max(0.1f, output);
    }

    @Override
    public float getBaseOutput() {
        return this.baseOutput;
    }

    @Override
    public void setBaseOutput(float output) {
        this.baseOutput = Math.max(0.1f, output);
        this.output = this.baseOutput;
    }

    @Override
    public float getEffectiveDamage(float baseDamage) {
        return baseDamage * this.output;
    }

    @Override
    public boolean consume(float baseCost) {
        float actualCost = getEffectiveCost(baseCost);
        if (this.currentCE >= actualCost) {
            this.currentCE -= actualCost;
            return true;
        }
        return false;
    }

    @Override
    public void forceConsume(float baseCost) {
        float actualCost = getEffectiveCost(baseCost);
        this.currentCE -= actualCost;
    }

    @Override
    public void add(float amount) {
        this.currentCE = Math.min(this.currentCE + amount, this.maxCE);
    }

    @Override
    public void onTick() {
        // Passive regeneration every tick
        float regen = getRegenRate();
        if (regen > 0 && this.currentCE < this.maxCE) {
            this.currentCE = Math.min(this.currentCE + regen, this.maxCE);
        }
    }

    @Override
    public boolean isChanneling() {
        return this.channeling;
    }

    @Override
    public void setChanneling(boolean channeling) {
        this.channeling = channeling;
    }

    @Override
    public boolean isRCTActive() {
        return this.rctActive;
    }

    @Override
    public void setRCTActive(boolean active) {
        this.rctActive = active;
    }

    @Override
    public boolean hasSixEyes() {
        return this.sixEyes;
    }

    @Override
    public void setSixEyes(boolean has) {
        this.sixEyes = has;
    }

    @Override
    public int getMasteryPoints() {
        return this.masteryPoints;
    }

    @Override
    public void setMasteryPoints(int points) {
        this.masteryPoints = Math.max(0, points);
    }

    @Override
    public void addMasteryPoints(int points) {
        this.masteryPoints += points;
    }

    @Override
    public int getMasteryLevel() {
        return this.masteryLevel;
    }

    @Override
    public void setMasteryLevel(int level) {
        this.masteryLevel = Math.max(0, level);
        recalculateMaxCE(); // Mastery affects max CE
    }

    @Override
    public void addMasteryXP(int xp) {
        this.masteryXP += xp;
        // Level up logic
        while (this.masteryXP >= MASTERY_XP_PER_LEVEL && this.masteryLevel < 200) {
            this.masteryXP -= MASTERY_XP_PER_LEVEL;
            this.masteryLevel++;
            this.masteryPoints += 2; // 2 points per level
        }
        recalculateMaxCE();
    }

    @Override
    public int getMasteryXP() {
        return this.masteryXP;
    }

    @Override
    public int getFingersConsumed() {
        return this.fingersConsumed;
    }

    @Override
    public void setFingersConsumed(int count) {
        this.fingersConsumed = Math.max(0, count);
        recalculateMaxCE();
    }

    @Override
    public void addFinger() {
        this.fingersConsumed++;
        recalculateMaxCE();
    }

    @Override
    public void copyFrom(ICursedEnergy source) {
        this.currentCE = source.getCurrentCE();
        this.baseMaxCE = source.getBaseMaxCE();
        this.maxCE = source.getMaxCE();
        this.baseRegenRate = source.getBaseRegenRate();
        this.regenMultiplier = source.getRegenMultiplier();
        this.regenPaused = source.isRegenPaused();
        this.baseEfficiency = source.getBaseEfficiency();
        this.efficiency = source.getEfficiency();
        this.baseOutput = source.getBaseOutput();
        this.output = source.getOutput();
        this.channeling = source.isChanneling();
        this.rctActive = source.isRCTActive();
        this.sixEyes = source.hasSixEyes();
        this.masteryPoints = source.getMasteryPoints();
        this.masteryLevel = source.getMasteryLevel();
        this.masteryXP = source.getMasteryXP();
        this.fingersConsumed = source.getFingersConsumed();
    }

    // ============================================================
    // NBT Serialization
    // ============================================================

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putFloat("currentCE", this.currentCE);
        tag.putFloat("baseMaxCE", this.baseMaxCE);
        tag.putFloat("maxCE", this.maxCE);
        tag.putFloat("baseRegenRate", this.baseRegenRate);
        tag.putFloat("regenMultiplier", this.regenMultiplier);
        tag.putBoolean("regenPaused", this.regenPaused);
        tag.putFloat("baseEfficiency", this.baseEfficiency);
        tag.putFloat("efficiency", this.efficiency);
        tag.putFloat("baseOutput", this.baseOutput);
        tag.putFloat("output", this.output);
        tag.putBoolean("channeling", this.channeling);
        tag.putBoolean("rctActive", this.rctActive);
        tag.putBoolean("sixEyes", this.sixEyes);
        tag.putInt("masteryPoints", this.masteryPoints);
        tag.putInt("masteryLevel", this.masteryLevel);
        tag.putInt("masteryXP", this.masteryXP);
        tag.putInt("fingersConsumed", this.fingersConsumed);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag tag) {
        this.currentCE = tag.getFloat("currentCE");
        this.baseMaxCE = tag.getFloat("baseMaxCE");
        this.maxCE = tag.getFloat("maxCE");
        this.baseRegenRate = tag.getFloat("baseRegenRate");
        this.regenMultiplier = tag.getFloat("regenMultiplier");
        this.regenPaused = tag.getBoolean("regenPaused");
        this.baseEfficiency = tag.getFloat("baseEfficiency");
        this.efficiency = tag.getFloat("efficiency");
        this.baseOutput = tag.getFloat("baseOutput");
        this.output = tag.getFloat("output");
        this.channeling = tag.getBoolean("channeling");
        this.rctActive = tag.getBoolean("rctActive");
        this.sixEyes = tag.getBoolean("sixEyes");
        this.masteryPoints = tag.getInt("masteryPoints");
        this.masteryLevel = tag.getInt("masteryLevel");
        this.masteryXP = tag.getInt("masteryXP");
        this.fingersConsumed = tag.getInt("fingersConsumed");
    }
}
