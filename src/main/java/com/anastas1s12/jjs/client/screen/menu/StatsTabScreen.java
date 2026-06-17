package com.anastas1s12.jjs.client.screen.menu;

import com.anastas1s12.jjs.client.ClientCEData;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

/**
 * =============================================================================
 * STATS TAB — Player Statistics & Achievements
 * =============================================================================
 *
 * Displays overall player statistics:
 *
 *   TOP: General stats (playtime, kills, deaths, etc.)
 *   MIDDLE: Combat stats (damage dealt, Black Flashes landed, etc.)
 *   BOTTOM: Cursed Spirit kill counts by grade
 *
 * NOTE: This is a placeholder. Integrate with your actual stat tracking system.
 *
 * =============================================================================
 */
public class StatsTabScreen extends BaseMenuScreen {

    public StatsTabScreen() {
        super(Tab.STATS);
    }

    @Override
    protected void renderContent(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        int cx = contentLeft();
        int cy = contentTop();
        int cw = contentWidth();

        // ---- GENERAL STATS ----
        renderSectionTitle(graphics, cx, cy, "General");
        cy += 14;

        renderStatRow(graphics, cx + 5, cy, "Playtime", "12h 34m", 0xFFFFFFFF); cy += 14;
        renderStatRow(graphics, cx + 5, cy, "Deaths", "7", 0xFFFF5252); cy += 14;
        renderStatRow(graphics, cx + 5, cy, "Fingers Consumed", String.valueOf(ClientCEData.getFingersConsumed()), 0xFFD32F2F); cy += 20;

        // ---- COMBAT STATS ----
        renderSectionTitle(graphics, cx, cy, "Combat");
        cy += 14;

        renderStatRow(graphics, cx + 5, cy, "Damage Dealt", "145,230", 0xFFFF5722); cy += 14;
        renderStatRow(graphics, cx + 5, cy, "Black Flashes", "47", 0xFFFF1744); cy += 14;
        renderStatRow(graphics, cx + 5, cy, "Highest BF Chain", "5", 0xFFFFD700); cy += 14;
        renderStatRow(graphics, cx + 5, cy, "Domains Used", "12", 0xFFF44336); cy += 14;
        renderStatRow(graphics, cx + 5, cy, "Domain Clashes Won", "8", 0xFF4CAF50); cy += 20;

        // ---- CURSED SPIRIT KILLS ----
        renderSectionTitle(graphics, cx, cy, "Cursed Spirit Kills");
        cy += 14;

        renderStatRow(graphics, cx + 5, cy, "Grade 4 Spirits", "124", 0xFF9E9E9E); cy += 14;
        renderStatRow(graphics, cx + 5, cy, "Grade 3 Spirits", "67", 0xFFFFFFFF); cy += 14;
        renderStatRow(graphics, cx + 5, cy, "Grade 2 Spirits", "34", 0xFF4CAF50); cy += 14;
        renderStatRow(graphics, cx + 5, cy, "Grade 1 Spirits", "15", 0xFF00BCD4); cy += 14;
        renderStatRow(graphics, cx + 5, cy, "Special Grade", "3", 0xFFFFD700); cy += 14;
    }

    private void renderSectionTitle(GuiGraphics graphics, int x, int y, String title) {
        graphics.drawString(this.font,
                Component.literal(title).withStyle(ChatFormatting.BOLD),
                x + 5, y, 0xFFFFFFFF, false);
        graphics.hLine(x + 5, x + 120, y + 12, 0xFF444444);
    }

    private void renderStatRow(GuiGraphics graphics, int x, int y, String label, String value, int valueColor) {
        graphics.drawString(this.font, label, x, y, 0xFFAAAAAA, false);
        int valueW = this.font.width(value);
        graphics.drawString(this.font, value, x + 130 - valueW, y, valueColor, false);
    }
}
