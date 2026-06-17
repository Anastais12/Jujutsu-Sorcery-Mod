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
 * DOMAIN TAB SCREEN — Domain Expansion Mastery
 * =============================================================================
 *
 * Displays the player's Domain Expansion information:
 *
 *   TOP SECTION:
 *     - Domain name (large text)
 *     - Domain icon (64x64, centered)
 *     - Mastery progress bar (0-100%)
 *     - Domain stability bar
 *
 *   MIDDLE SECTION — Domain Stats:
 *     - Domain range / radius
 *     - Duration
 *     - Guaranteed hit accuracy
 *     - Clash win rate
 *     - Refinement level
 *
 *   BOTTOM SECTION — Refinement Upgrades:
 *     - List of unlockable upgrades for the domain
 *     - Each with name, description, cost, and unlock status
 *
 * TEXTURE PATHS:
 *   - Domain icon: textures/gui/menu/domain_icon.png
 *   - Upgrade icons: textures/gui/menu/domain_upgrades.png
 *
 * =============================================================================
 */
public class DomainTabScreen extends BaseMenuScreen {

    // ============================================================
    // TEXTURES
    // ============================================================

    /** Domain icon texture (64x64) */
    public static final ResourceLocation DOMAIN_ICON =
            ResourceLocation.fromNamespaceAndPath(JujutsuSorcery.MOD_ID, "textures/gui/menu/domain_icon.png");

    /** Upgrade node background */
    public static final ResourceLocation UPGRADE_NODE =
            ResourceLocation.fromNamespaceAndPath(JujutsuSorcery.MOD_ID, "textures/gui/menu/upgrade_node.png");

    // ============================================================
    // FIELDS
    // ============================================================

    // Domain data — populate from player capability
    private String domainName = "Infinite Void";
    private String domainInnateTechnique = "Limitless";
    private float domainMastery = 0.0f; // 0.0 to 1.0
    private float domainStability = 0.0f; // 0.0 to 1.0
    private int domainRange = 80; // blocks
    private int domainDuration = 120; // seconds
    private float guaranteedHitRate = 0.0f; // 0.0 to 1.0
    private int refinementLevel = 0;

    // ============================================================
    // CONSTRUCTOR
    // ============================================================

    public DomainTabScreen() {
        super(Tab.DOMAIN);

        // Load domain data from player capability
        loadDomainData();
    }

    /**
     * Loads domain data from the player's capability.
     * Call this to refresh data from the server.
     */
    private void loadDomainData() {
        int masteryLevel = ClientCEData.getMasteryLevel();

        // Calculate domain values based on mastery
        // Domain unlocks at mastery 50
        if (masteryLevel >= 50) {
            this.domainMastery = Math.min(1.0f, (masteryLevel - 50) / 50.0f);
            this.domainStability = Math.min(1.0f, masteryLevel / 100.0f);
            this.domainRange = 50 + (int) (domainMastery * 100);
            this.domainDuration = 60 + (int) (domainMastery * 180);
            this.guaranteedHitRate = domainMastery;
            this.refinementLevel = (masteryLevel - 50) / 10;
        }
    }

    // ============================================================
    // CONTENT RENDER
    // ============================================================

    @Override
    protected void renderContent(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        int cx = contentLeft();
        int cy = contentTop();
        int cw = contentWidth();

        // ---- DOMAIN HEADER ----
        renderDomainHeader(graphics, cx, cy, cw, mouseX, mouseY);
        cy += 90;

        // ---- MASTERY & STABILITY BARS ----
        renderMasteryBars(graphics, cx, cy, cw);
        cy += 55;

        // ---- DOMAIN STATS GRID ----
        renderDomainStats(graphics, cx, cy, cw);
        cy += 85;

        // ---- REFINEMENT UPGRADES ----
        renderRefinementUpgrades(graphics, cx, cy, cw, mouseX, mouseY);
    }

    // ============================================================
    // HEADER — Domain Name & Icon
    // ============================================================

    private void renderDomainHeader(GuiGraphics graphics, int x, int y, int width, int mouseX, int mouseY) {
        // Domain icon (64x64, left side)
        int iconX = x + 10;
        int iconY = y + 5;

        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);

        // Fallback: draw colored square if texture missing
        graphics.fill(iconX, iconY, iconX + 64, iconY + 64, 0xFF333333);
        graphics.renderOutline(iconX, iconY, 64, 64, 0xFFFF1744);

        // Domain symbol (placeholder)
        graphics.drawCenteredString(this.font, "\u2726", iconX + 32, iconY + 24, 0xFFFF1744);

        // ---- Text info (right of icon) ----
        int textX = x + 85;
        int textY = y + 8;

        // "Domain Expansion" label
        graphics.drawString(this.font, "Domain Expansion", textX, textY, 0xFFFF5252, false);
        textY += 12;

        // Domain name (large)
        graphics.drawString(this.font,
                Component.literal(domainName).withStyle(net.minecraft.ChatFormatting.BOLD),
                textX, textY, 0xFFFFFFFF, false);
        textY += 14;

        // Innate technique
        graphics.drawString(this.font,
                "Technique: " + domainInnateTechnique, textX, textY, 0xFFAAAAAA, false);
        textY += 14;

        // Status
        boolean unlocked = ClientCEData.getMasteryLevel() >= 50;
        String status = unlocked ? "Unlocked" : "Locked (Mastery 50 required)";
        int statusColor = unlocked ? 0xFF4CAF50 : 0xFFFF5252;
        graphics.drawString(this.font, status, textX, textY, statusColor, false);

        // Divider
        graphics.hLine(x, x + width, y + 78, 0xFF444444);
    }

    // ============================================================
    // MASTERY & STABILITY BARS
    // ============================================================

    private void renderMasteryBars(GuiGraphics graphics, int x, int y, int width) {
        int barWidth = (width - 20) / 2;

        // Domain Mastery bar (left)
        graphics.drawString(this.font, "Domain Mastery", x + 5, y, 0xFFAAAAAA, false);
        renderProgressBar(graphics, x + 5, y + 12, barWidth, 14,
                domainMastery,
                String.format("%.0f%%", domainMastery * 100),
                0xFF673AB7, 0xFF333333);

        // Domain Stability bar (right)
        int rightX = x + 15 + barWidth;
        graphics.drawString(this.font, "Domain Stability", rightX, y, 0xFFAAAAAA, false);
        renderProgressBar(graphics, rightX, y + 12, barWidth, 14,
                domainStability,
                getStabilityLabel(),
                getStabilityColor(), 0xFF333333);
    }

    private String getStabilityLabel() {
        if (domainStability < 0.2f) return "Unstable";
        if (domainStability < 0.4f) return "Weak";
        if (domainStability < 0.6f) return "Stable";
        if (domainStability < 0.8f) return "Strong";
        return "Perfect";
    }

    private int getStabilityColor() {
        if (domainStability < 0.2f) return 0xFFFF0000;
        if (domainStability < 0.4f) return 0xFFFF5722;
        if (domainStability < 0.6f) return 0xFFFFD700;
        if (domainStability < 0.8f) return 0xFF4CAF50;
        return 0xFF00E676;
    }

    // ============================================================
    // DOMAIN STATS GRID
    // ============================================================

    private void renderDomainStats(GuiGraphics graphics, int x, int y, int width) {
        graphics.drawString(this.font, "Domain Statistics", x + 5, y, 0xFFFFFFFF, false);
        y += 14;

        int col1X = x + 5;
        int col2X = x + width / 2 + 5;
        int rowY = y;

        // Column 1
        renderStatPair(graphics, col1X, rowY, "Range:", domainRange + " blocks", 0xFF00BCD4);
        rowY += 18;
        renderStatPair(graphics, col1X, rowY, "Duration:", formatDuration(domainDuration), 0xFFFFD700);
        rowY += 18;
        renderStatPair(graphics, col1X, rowY, "Guaranteed Hit:", String.format("%.0f%%", guaranteedHitRate * 100), 0xFFFF1744);

        // Column 2
        rowY = y;
        renderStatPair(graphics, col2X, rowY, "Refinement:", "Lv. " + refinementLevel, 0xFF9C27B0);
        rowY += 18;
        renderStatPair(graphics, col2X, rowY, "Barrier Strength:", String.format("%.0f%%", domainStability * 100), 0xFF4CAF50);
        rowY += 18;
        renderStatPair(graphics, col2X, rowY, "Clash Power:", String.format("%.0f", domainMastery * 100), 0xFF2196F3);

        // Divider
        graphics.hLine(x, x + width, y + 60, 0xFF444444);
    }

    private void renderStatPair(GuiGraphics graphics, int x, int y, String label, String value, int valueColor) {
        graphics.drawString(this.font, label, x, y, 0xFF999999, false);
        int valueWidth = this.font.width(value);
        graphics.drawString(this.font, value, x + 90 - valueWidth, y, valueColor, false);
    }

    private String formatDuration(int seconds) {
        int mins = seconds / 60;
        int secs = seconds % 60;
        if (mins > 0) {
            return String.format("%dm %ds", mins, secs);
        }
        return secs + "s";
    }

    // ============================================================
    // REFINEMENT UPGRADES
    // ============================================================

    private void renderRefinementUpgrades(GuiGraphics graphics, int x, int y, int width, int mouseX, int mouseY) {
        graphics.drawString(this.font, "Refinement Upgrades", x + 5, y, 0xFFFFFFFF, false);
        y += 14;

        // List of example upgrades
        String[][] upgrades = {
                {"Barrier Hardening", "+15% domain stability", "50"},
                {"Range Extension", "+20 block radius", "65"},
                {"Duration Boost", "+30 seconds duration", "80"},
                {"Sure-Hit Enhancement", "+10% guaranteed hit", "100"},
        };

        boolean domainUnlocked = ClientCEData.getMasteryLevel() >= 50;

        for (String[] upgrade : upgrades) {
            String name = upgrade[0];
            String desc = upgrade[1];
            int reqMastery = Integer.parseInt(upgrade[2]);
            boolean unlocked = domainUnlocked && ClientCEData.getMasteryLevel() >= reqMastery;

            // Upgrade row background
            int bgColor = unlocked ? 0xFF1B2E1B : 0xFF2E1B1B;
            graphics.fill(x + 5, y, x + width - 5, y + 28, bgColor);

            // Upgrade icon placeholder
            int iconColor = unlocked ? 0xFF4CAF50 : 0xFFFF5252;
            graphics.fill(x + 10, y + 4, x + 20, y + 14, iconColor);

            // Name
            graphics.drawString(this.font, name, x + 24, y + 3,
                    unlocked ? 0xFFFFFFFF : 0xFF888888, false);

            // Description
            graphics.drawString(this.font, desc, x + 24, y + 15, 0xFFAAAAAA, false);

            // Requirement
            String req = "Mastery " + reqMastery;
            int reqW = this.font.width(req);
            graphics.drawString(this.font, req, x + width - 15 - reqW, y + 8,
                    unlocked ? 0xFF4CAF50 : 0xFFFF5252, false);

            // Status indicator
            String status = unlocked ? "UNLOCKED" : "LOCKED";
            int statusW = this.font.width(status);
            graphics.drawString(this.font, status, x + width - 15 - statusW, y + 16,
                    unlocked ? 0xFF4CAF50 : 0xFFFF5252, false);

            y += 30;
        }
    }
}
