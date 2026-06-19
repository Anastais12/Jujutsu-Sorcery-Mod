package com.anastas1s12.jjs.client;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

public class DistortionClientState {

    private static boolean detected = false;
    private static int timer = 0;
    private static final int DURATION_TICKS = 20; // 1 second

    public static void onPacket(boolean newDetected) {
        // Only trigger when it switches from false -> true
        if (newDetected && !detected && !ClientSorcererState.isSorcererModeActive()) {
            timer = DURATION_TICKS;
            var player = Minecraft.getInstance().player;
            if (player != null) {
                player.sendSystemMessage(Component.literal("You sense cursed energy nearby..."));
            }
        }
        detected = newDetected;
    }

    public static void clientTick() {
        if (timer > 0) {
            // If sorcerer mode opens, stop effect immediately
            if (ClientSorcererState.isSorcererModeActive()) {
                timer = 0;
                return;
            }
            timer--;
        }
    }

    public static boolean shouldRenderDistortion() {
        return timer > 0 && !ClientSorcererState.isSorcererModeActive();
    }
}