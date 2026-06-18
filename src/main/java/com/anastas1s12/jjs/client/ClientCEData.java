package com.anastas1s12.jjs.client;

import com.anastas1s12.jjs.capability.ICursedEnergy;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.util.INBTSerializable;

/**
 * Client-side cache of Cursed Energy data.
 * Updated by CursedEnergySyncS2CPacket whenever the server syncs data.
 */
public class ClientCEData {

    private static float currentCE = 0;
    private static float maxCE = 100;
    private static float baseMaxCE = 100;
    private static float regenRate = 0;
    private static float efficiency = 0;
    private static float output = 1.0f;
    private static int masteryLevel = 0;
    private static int masteryPoints = 0;
    private static int masteryXP = 0;
    private static int fingersConsumed = 0;
    private static boolean rctActive = false;
    private static boolean sixEyes = false;

    private static float baseRegenRate = 0;
    private static float regenMultiplier = 1.0f;
    private static boolean regenPaused = false;
    private static float baseEfficiency = 0;
    private static boolean channeling = false;

    /**
     * Update all cached values from server.
     */
    public static void set(float currentCE, float maxCE, float baseMaxCE, float regenRate,
                           float efficiency, float output, int masteryLevel, int masteryPoints,
                           int masteryXP, int fingersConsumed, boolean rctActive, boolean sixEyes) {
        ClientCEData.currentCE = currentCE;
        ClientCEData.maxCE = maxCE;
        ClientCEData.baseMaxCE = baseMaxCE;
        ClientCEData.regenRate = regenRate;
        ClientCEData.efficiency = efficiency;
        ClientCEData.output = output;
        ClientCEData.masteryLevel = masteryLevel;
        ClientCEData.masteryPoints = masteryPoints;
        ClientCEData.masteryXP = masteryXP;
        ClientCEData.fingersConsumed = fingersConsumed;
        ClientCEData.rctActive = rctActive;
        ClientCEData.sixEyes = sixEyes;
    }

    /**
     * Returns a client-side proxy that implements ICursedEnergy.
     */
    public static ICursedEnergy getPlayerData() {
        return new ClientCursedEnergyProxy();
    }

    // ============================================================
    // Direct Getters
    // ============================================================
    public static float getCurrentCE() { return currentCE; }
    public static float getMaxCE() { return maxCE; }
    public static float getBaseMaxCE() { return baseMaxCE; }
    public static float getRegenRate() { return regenRate; }
    public static float getEfficiency() { return efficiency; }
    public static float getOutput() { return output; }
    public static int getMasteryLevel() { return masteryLevel; }
    public static int getMasteryPoints() { return masteryPoints; }
    public static int getMasteryXP() { return masteryXP; }
    public static int getFingersConsumed() { return fingersConsumed; }
    public static boolean isRctActive() { return rctActive; }
    public static boolean hasSixEyes() { return sixEyes; }

    public static float getCERatio() {
        if (maxCE <= 0) return 0;
        return currentCE / maxCE;
    }

    public static boolean hasData() {
        return Minecraft.getInstance().player != null;
    }

    // ============================================================
    // FULL CLIENT PROXY IMPLEMENTATION
    // ============================================================
    private static class ClientCursedEnergyProxy implements ICursedEnergy {

        @Override
        public float getCurrentCE() {
            return ClientCEData.currentCE;
        }

        @Override
        public void setCurrentCE(float amount) {
            // Client should not modify server data
            // (Sync will come from server)
        }

        @Override
        public float getMaxCE() {
            return ClientCEData.maxCE;
        }

        @Override
        public float getBaseMaxCE() {
            return ClientCEData.baseMaxCE;
        }

        @Override
        public void setBaseMaxCE(float amount) {
            // No-op on client
        }

        @Override
        public void recalculateMaxCE() {
            // No-op on client
        }

        @Override
        public float getRegenRate() {
            return ClientCEData.regenRate;
        }

        @Override
        public void setRegenRate(float rate) {
            // No-op
        }

        @Override
        public float getBaseRegenRate() {
            return ClientCEData.baseRegenRate;
        }

        @Override
        public void setBaseRegenRate(float rate) {
            // No-op
        }

        @Override
        public float getRegenMultiplier() {
            return ClientCEData.regenMultiplier;
        }

        @Override
        public void setRegenMultiplier(float multiplier) {
            // No-op
        }

        @Override
        public boolean isRegenPaused() {
            return ClientCEData.regenPaused;
        }

        @Override
        public void setRegenPaused(boolean paused) {
            // No-op
        }

        @Override
        public float getEfficiency() {
            return ClientCEData.efficiency;
        }

        @Override
        public void setEfficiency(float efficiency) {
            // No-op
        }

        @Override
        public float getBaseEfficiency() {
            return ClientCEData.baseEfficiency;
        }

        @Override
        public void setBaseEfficiency(float efficiency) {
            // No-op
        }

        @Override
        public float getEffectiveCost(float baseCost) {
            return baseCost * (1.0f - getEfficiency());
        }

        @Override
        public float getOutput() {
            return ClientCEData.output;
        }

        @Override
        public void setOutput(float output) {
            // No-op
        }

        @Override
        public float getBaseOutput() {
            return ClientCEData.output; // fallback
        }

        @Override
        public void setBaseOutput(float output) {
            // No-op
        }

        @Override
        public float getEffectiveDamage(float baseDamage) {
            return baseDamage * getOutput();
        }

        @Override
        public boolean consume(float baseCost) {
            return false; // Client shouldn't consume
        }

        @Override
        public void forceConsume(float baseCost) {
            // No-op
        }

        @Override
        public void add(float amount) {
            // No-op
        }

        @Override
        public void onTick() {
            // No-op on client
        }

        @Override
        public boolean isChanneling() {
            return ClientCEData.channeling;
        }

        @Override
        public void setChanneling(boolean channeling) {
            // No-op
        }

        @Override
        public boolean isRCTActive() {
            return ClientCEData.rctActive;
        }

        @Override
        public void setRCTActive(boolean active) {
            // No-op
        }

        @Override
        public boolean hasSixEyes() {
            return ClientCEData.sixEyes;
        }

        @Override
        public void setSixEyes(boolean has) {
            // No-op
        }

        @Override
        public int getMasteryPoints() {
            return ClientCEData.masteryPoints;
        }

        @Override
        public void setMasteryPoints(int points) {
            // No-op
        }

        @Override
        public void addMasteryPoints(int points) {
            // No-op
        }

        @Override
        public int getMasteryLevel() {
            return ClientCEData.masteryLevel;
        }

        @Override
        public void setMasteryLevel(int level) {
            // No-op
        }

        @Override
        public void addMasteryXP(int xp) {
            // No-op
        }

        @Override
        public int getMasteryXP() {
            return ClientCEData.masteryXP;
        }

        @Override
        public int getFingersConsumed() {
            return ClientCEData.fingersConsumed;
        }

        @Override
        public void setFingersConsumed(int count) {
            // No-op
        }

        @Override
        public void addFinger() {
            // No-op
        }

        @Override
        public float getCERatio() {
            return ClientCEData.getCERatio();
        }

        @Override
        public boolean isAbovePercent(float percent) {
            return getCERatio() >= percent;
        }

        @Override
        public void copyFrom(ICursedEnergy source) {
            // No-op on client
        }

        @Override
        public CompoundTag serializeNBT() {
            return new CompoundTag(); // Not used on client
        }

        @Override
        public void deserializeNBT(CompoundTag nbt) {
            // Not used on client
        }
    }
}