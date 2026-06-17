package com.anastas1s12.jjs.client;

import net.minecraft.client.Minecraft;

/**
 * Client-side cache of Cursed Energy data.
 * Updated by CursedEnergySyncS2CPacket whenever the server syncs data.
 * Used by the HUD overlay to display current values without querying the capability.
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

    /**
     * Update all cached values from a server sync packet.
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

    /**
     * Whether the local player has any CE data (not a fresh login).
     */
    public static boolean hasData() {
        return Minecraft.getInstance().player != null;
    }
}
