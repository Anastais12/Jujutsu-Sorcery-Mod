package com.anastas1s12.jjs.client.gui;

import com.anastas1s12.jjs.JujutsuSorcery;
import com.anastas1s12.jjs.client.ClientCEData;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;

/**
 * Renders the Cursed Energy bar HUD overlay above the hotbar.
 * Displays current/max CE as a bar with numeric readout.
 * Shows additional info when Six Eyes is active.
 */
public class CursedEnergyHudOverlay implements IGuiOverlay {

    public static final CursedEnergyHudOverlay INSTANCE = new CursedEnergyHudOverlay();

    // Texture for the bar frame/background (you'll need to create these assets)
    // For now we render procedurally with colored rectangles for zero-dependency setup.

    // Bar colors
    private static final int COLOR_BG = 0xFF1A1A1A;         // Dark background
    private static final int COLOR_CE_FILL = 0xFF00E676;     // Vibrant green (cursed energy)
    private static final int COLOR_CE_LOW = 0xFFFF5252;      // Red when low
    private static final int COLOR_CE_HIGH = 0xFF69F0AE;     // Light green when high
    private static final int COLOR_BORDER = 0xFF424242;      // Border color
    private static final int COLOR_TEXT = 0xFFFFFFFF;        // White text
    private static final int COLOR_SIX_EYES = 0xFF00B0FF;    // Cyan for Six Eyes
    private static final int COLOR_RCT = 0xFFE040FB;         // Purple for RCT active

    // Bar dimensions
    private static final int BAR_WIDTH = 100;
    private static final int BAR_HEIGHT = 10;

    private CursedEnergyHudOverlay() {}

    @Override
    public void render(ForgeGui gui, GuiGraphics graphics, float partialTick, int screenWidth, int screenHeight) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;
        if (mc.options.hideGui) return;

        float current = ClientCEData.getCurrentCE();
        float max = ClientCEData.getMaxCE();
        if (max <= 0) return;

        float ratio = current / max;
        boolean sixEyes = ClientCEData.hasSixEyes();
        boolean rctActive = ClientCEData.isRctActive();

        // Position: above the hotbar (hotbar is at screenHeight - 22, we go 15px above that)
        int hotbarY = screenHeight - 22;
        int barX = screenWidth / 2 - BAR_WIDTH / 2;
        int barY = hotbarY - 18;

        RenderSystem.enableBlend();

        // --- Background bar ---
        graphics.fill(barX - 1, barY - 1, barX + BAR_WIDTH + 1, barY + BAR_HEIGHT + 1, COLOR_BORDER);
        graphics.fill(barX, barY, barX + BAR_WIDTH, barY + BAR_HEIGHT, COLOR_BG);

        // --- Fill bar ---
        int fillWidth = (int) (BAR_WIDTH * ratio);
        int fillColor = sixEyes ? COLOR_SIX_EYES : getFillColor(ratio, rctActive);

        if (fillWidth > 0) {
            graphics.fill(barX, barY, barX + fillWidth, barY + BAR_HEIGHT, fillColor);
        }

        // --- RCT indicator (pulsing overlay) ---
        if (rctActive) {
            int pulseAlpha = (int) ((Math.sin(System.currentTimeMillis() / 200.0) * 0.3 + 0.5) * 255);
            int pulseColor = (pulseAlpha << 24) | (COLOR_RCT & 0x00FFFFFF);
            graphics.fill(barX, barY, barX + fillWidth, barY + BAR_HEIGHT, pulseColor);
        }

        // --- Text: "CE: current / max" ---
        String text = String.format("CE: %.0f / %.0f", current, max);
        int textWidth = mc.font.width(text);
        int textX = screenWidth / 2 - textWidth / 2;
        int textY = barY - 10;

        // Text shadow for readability
        graphics.drawString(mc.font, text, textX + 1, textY + 1, 0xFF000000, false);
        graphics.drawString(mc.font, text, textX, textY, COLOR_TEXT, false);

        // --- Mastery display (small, below bar) ---
        int mastery = ClientCEData.getMasteryLevel();
        if (mastery > 0) {
            String masteryText = String.format("Mastery: %d", mastery);
            int mWidth = mc.font.width(masteryText);
            int mX = screenWidth / 2 - mWidth / 2;
            graphics.drawString(mc.font, masteryText, mX, barY + BAR_HEIGHT + 2, 0xFFAAAAAA, false);
        }

        // --- Six Eyes indicator ---
        if (sixEyes) {
            String seText = "\u2726 Six Eyes \u2726"; // Star symbols
            int seWidth = mc.font.width(seText);
            int seX = screenWidth / 2 - seWidth / 2;
            graphics.drawString(mc.font, seText, seX, textY - 10, COLOR_SIX_EYES, false);
        }

        RenderSystem.disableBlend();
    }

    /**
     * Get the bar fill color based on CE percentage.
     */
    private int getFillColor(float ratio, boolean rctActive) {
        if (rctActive) {
            return COLOR_RCT;
        }
        if (ratio <= 0.2f) {
            return COLOR_CE_LOW;
        } else if (ratio >= 0.8f) {
            return COLOR_CE_HIGH;
        } else {
            return COLOR_CE_FILL;
        }
    }
}
