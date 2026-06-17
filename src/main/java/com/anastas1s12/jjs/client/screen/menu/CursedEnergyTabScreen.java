package com.anastas1s12.jjs.client.screen.menu;

import com.anastas1s12.jjs.JujutsuSorcery;
import com.anastas1s12.jjs.client.ClientCEData;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

/**
 * =============================================================================
 * CURSED ENERGY TAB — Detailed CE Statistics
 * =============================================================================
 *
 * Displays comprehensive Cursed Energy information:
 *
 *   TOP: Large CE bar showing current / max with percentage
 *
 *   STATS GRID: Two columns of CE stats with icons and bars
 *     - Cursed Energy (current/max)
 *     - Cursed Energy Output
 *     - Cursed Energy Efficiency
 *     - Cursed Energy Control
 *     - CE Regeneration Rate
 *     - CE Reserve Capacity
 *
 *   BOTTOM: Historical info / tips
 *     - How to increase CE (tips)
 *     - Recent CE milestones
 *
 * TEXTURE PATHS:
 *   - Large CE bar background: textures/gui/menu/ce_bar_large.png
 *   - Stat icons: textures/gui/menu/stat_icons.png (16x16 sheet)
 * =============================================================================
 */
public class CursedEnergyTabScreen extends BaseMenuScreen {

    /** Large decorative CE icon */
    public static final ResourceLocation CE_ICON =
            ResourceLocation.fromNamespaceAndPath(JujutsuSorcery.MOD_ID, "textures/gui/menu/ce_icon_large.png");

    public CursedEnergyTabScreen() {
        super(Tab.CURSED_ENERGY);
    }

    @Override
    protected void renderContent(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        int cx = contentLeft();
        int cy = contentTop();
        int cw = contentWidth();

        // ---- LARGE CE BAR ----
        renderLargeCEBar(graphics, cx, cy, cw);
        cy += 55;

        // ---- STATS GRID (2 columns) ----
        graphics.drawString(this.font, "Cursed Energy Statistics", cx + 5, cy, 0xFFFFFFFF, false);
        cy += 14;

        int colW = (cw - 20) / 2;
        int col1X = cx + 5;
        int col2X = cx + 15 + colW;

        // Column 1
        renderCEStat(graphics, col1X, cy, "CE Output",
                String.format("%.2fx", ClientCEData.getOutput()),
                ClientCEData.getOutput() / 5.0f, 0xFFFF5722);
        cy += 40;

        renderCEStat(graphics, col1X, cy, "Efficiency",
                String.format("%.1f%%", ClientCEData.getEfficiency() * 100),
                ClientCEData.getEfficiency() / 0.95f, 0xFF2196F3);
        cy += 40;

        renderCEStat(graphics, col1X, cy, "Regeneration",
                String.format("%.2f/tick", ClientCEData.getRegenRate()),
                ClientCEData.getRegenRate() / 0.5f, 0xFF00E676);

        // Column 2 (reset Y)
        cy = contentTop() + 69;

        renderCEStat(graphics, col2X, cy, "Control",
                getControlText(), getControlRatio(), 0xFF9C27B0);
        cy += 40;

        renderCEStat(graphics, col2X, cy, "Reserves",
                String.format("%.0f base", ClientCEData.getBaseMaxCE()),
                ClientCEData.getBaseMaxCE() / 1000.0f, 0xFFFFD700);
        cy += 40;

        renderCEStat(graphics, col2X, cy, "Fingers",
                String.valueOf(ClientCEData.getFingersConsumed()),
                ClientCEData.getFingersConsumed() / 20.0f, 0xFFD32F2F);

        // ---- DIVIDER ----
        cy = contentTop() + 200;
        graphics.hLine(cx, cx + cw, cy, 0xFF444444);
        cy += 8;

        // ---- TIPS SECTION ----
        renderTipsSection(graphics, cx, cy, cw);
    }

    /**
     * Renders the large CE bar at the top of the content area.
     */
    private void renderLargeCEBar(GuiGraphics graphics, int x, int y, int width) {
        int barHeight = 30;
        float ratio = ClientCEData.getCERatio();
        float current = ClientCEData.getCurrentCE();
        float max = ClientCEData.getMaxCE();

        // CE Icon (left side, 24x24)
        graphics.fill(x + 3, y + 3, x + 27, y + 27, 0xFF00E676);
        graphics.drawCenteredString(this.font, "CE", x + 15, y + 9, 0xFF000000);

        int barX = x + 35;
        int barW = width - 40;

        // Background
        graphics.fill(barX, y + 5, barX + barW, y + 5 + barHeight, 0xFF1A1A1A);
        graphics.renderOutline(barX, y + 5, barW, barHeight, 0xFF444444);

        // Fill with gradient-like effect (solid for now)
        int fillW = (int) (barW * ratio);
        int fillColor = ratio > 0.8f ? 0xFF69F0AE : (ratio > 0.3f ? 0xFF00E676 : 0xFFFF5252);
        if (fillW > 0) {
            graphics.fill(barX, y + 5, barX + fillW, y + 5 + barHeight, fillColor);
        }

        // Text overlaid on bar
        String barText = String.format("%.0f / %.0f  (%.1f%%)", current, max, ratio * 100);
        int textW = this.font.width(barText);
        int textColor = ratio > 0.5f ? 0xFF000000 : 0xFFFFFFFF;
        graphics.drawString(this.font, barText,
                barX + (barW - textW) / 2, y + 12, textColor, false);

        // RCT indicator
        if (ClientCEData.isRctActive()) {
            String rctText = "RCT ACTIVE";
            int rctW = this.font.width(rctText);
            int rctX = barX + barW - rctW - 5;
            graphics.drawString(this.font, rctText, rctX, y - 10, 0xFFE040FB, false);
        }
    }

    /**
     * Renders a single CE stat with icon, label, value, and a bar.
     */
    private void renderCEStat(GuiGraphics graphics, int x, int y, String label,
                               String value, float ratio, int color) {
        int barW = 140;

        // Icon placeholder (colored square)
        graphics.fill(x, y, x + 14, y + 14, color);

        // Label
        graphics.drawString(this.font, label, x + 18, y, 0xFFCCCCCC, false);

        // Value (right aligned)
        int valW = this.font.width(value);
        graphics.drawString(this.font, value, x + barW - valW, y, color, false);

        // Bar below
        int barY = y + 16;
        graphics.fill(x, barY, x + barW, barY + 6, 0xFF333333);
        int fillW = (int) (barW * Math.max(0, Math.min(1, ratio)));
        if (fillW > 0) {
            graphics.fill(x, barY, x + fillW, barY + 6, color);
        }
    }

    /**
     * Renders tips on how to increase CE.
     */
    private void renderTipsSection(GuiGraphics graphics, int x, int y, int width) {
        graphics.drawString(this.font, "How to Improve", x + 5, y, 0xFFFFD700, false);
        y += 14;

        String[] tips = {
                "\u2022 Defeat cursed spirits to gain Mastery XP",
                "\u2022 Consume Sukuna Fingers to increase Max CE",
                "\u2022 Train with abilities to improve Efficiency",
                "\u2022 Use Black Flash successfully to boost Output",
                "\u2022 Master Reverse Cursed Technique at Mastery 50",
        };

        for (String tip : tips) {
            graphics.drawString(this.font, tip, x + 10, y, 0xFFAAAAAA, false);
            y += 12;
        }
    }

    // ---- Control stat helpers (inherited from BaseMenuScreen) ----
}
