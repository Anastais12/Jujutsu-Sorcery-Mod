package com.anastas1s12.jjs.client.screen.menu;

import com.anastas1s12.jjs.system.ability.Grade;
import com.anastas1s12.jjs.client.ClientCEData;
import com.anastas1s12.jjs.client.ClientTechniqueData;
import com.anastas1s12.jjs.capability.CursedEnergy;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

/**
 * The "Stats" tab of the JJS sorcerer menu (tab index 1).
 *
 * Uses screen_template.png (512×256) as its background.
 * Renders CE stats, mastery progress, and grade into the center content area.
 *
 * All layout is driven by the texture-space coordinates inherited from
 * AbstractMenuScreen (centerX/Y/W/H, leftSidebarX/Y/W/H, etc.) which are
 * automatically scaled to the current screen size.
 */
public class StatsTabScreen extends AbstractMenuScreen {

    private static final int TAB_INDEX = 1;

    // Text colors
    private static final int COLOR_HEADER  = 0xFFFFD700;
    private static final int COLOR_LABEL   = 0xFF888899;
    private static final int COLOR_VALUE   = 0xFFFFFFFF;
    private static final int COLOR_DIVIDER = 0xFF222233;
    private static final int COLOR_BAR_BG  = 0xFF111120;
    private static final int COLOR_BAR_CE  = 0xFF00E676;
    private static final int COLOR_BAR_XP  = 0xFF42A5F5;

    // Padding inside the center area (in screen pixels, not texture pixels,
    // since we just need a small consistent inset)
    private static final int PAD = 10;

    public StatsTabScreen() {
        super(Component.literal("Stats"), TAB_INDEX, TEX_TEMPLATE);
    }

    @Override
    protected void renderContent(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        int x = centerX + PAD;
        int y = centerY + PAD;
        int barW = centerW - PAD * 2; // bars stretch across the center area

        // ---- Grade title ---------------------------------------------------
        int mastery = ClientCEData.getMasteryLevel();
        Grade grade = Grade.fromMastery(mastery);
        graphics.drawString(font, grade.getTitle(), x, y, grade.getColor(), false);
        y += font.lineHeight + 4;
        graphics.fill(x, y, x + barW, y + 1, COLOR_DIVIDER);
        y += 5;

        // ---- Cursed Energy -------------------------------------------------
        graphics.drawString(font, "Cursed Energy", x, y, COLOR_HEADER, false);
        y += font.lineHeight + 3;

        float current = ClientCEData.getCurrentCE();
        float max     = ClientCEData.getMaxCE();
        float ratio   = max > 0 ? current / max : 0f;
        int barH      = Math.max(4, toScreenH(5)); // ~5 texture-px tall bar

        // CE fill bar
        graphics.fill(x, y, x + barW, y + barH, COLOR_BAR_BG);
        graphics.fill(x, y, x + (int) (barW * ratio), y + barH, COLOR_BAR_CE);
        y += barH + 3;

        // CE numeric
        graphics.drawString(font,
                String.format("%.0f / %.0f  (%.1f%%)", current, max, ratio * 100),
                x, y, COLOR_VALUE, false);
        y += font.lineHeight + 3;

        // CE stat rows
        drawStatRow(graphics, x, y, barW, "Base Max",   String.format("%.0f",  ClientCEData.getBaseMaxCE()));  y += font.lineHeight + 2;
        drawStatRow(graphics, x, y, barW, "Regen",      String.format("%.2f/t", ClientCEData.getRegenRate())); y += font.lineHeight + 2;
        drawStatRow(graphics, x, y, barW, "Efficiency", String.format("%.1f%%", ClientCEData.getEfficiency() * 100)); y += font.lineHeight + 2;
        drawStatRow(graphics, x, y, barW, "Output",     String.format("%.2fx",  ClientCEData.getOutput()));    y += font.lineHeight + 2;

        // Flags row
        boolean rct     = ClientCEData.isRctActive();
        boolean sixEyes = ClientCEData.hasSixEyes();
        graphics.drawString(font, "RCT: "      + (rct     ? "\u00A7dON"  : "\u00A77OFF"), x,          y, COLOR_VALUE, false);
        graphics.drawString(font, "Six Eyes: " + (sixEyes ? "\u00A7bYES" : "\u00A77NO"),  x + barW/2, y, COLOR_VALUE, false);
        y += font.lineHeight + 6;

        graphics.fill(x, y, x + barW, y + 1, COLOR_DIVIDER);
        y += 5;

        // ---- Mastery -------------------------------------------------------
        graphics.drawString(font, "Mastery", x, y, COLOR_HEADER, false);
        y += font.lineHeight + 3;

        int xp       = ClientCEData.getMasteryXP();
        int xpPerLvl = CursedEnergy.MASTERY_XP_PER_LEVEL;
        float xpRatio = (float) xp / xpPerLvl;

        // XP fill bar
        graphics.fill(x, y, x + barW, y + barH, COLOR_BAR_BG);
        graphics.fill(x, y, x + (int) (barW * xpRatio), y + barH, COLOR_BAR_XP);
        y += barH + 3;

        graphics.drawString(font,
                String.format("Level %d  —  %d / %d XP", mastery, xp, xpPerLvl),
                x, y, COLOR_VALUE, false);
        y += font.lineHeight + 3;

        drawStatRow(graphics, x, y, barW, "Mastery Points", String.valueOf(ClientCEData.getMasteryPoints())); y += font.lineHeight + 2;
        drawStatRow(graphics, x, y, barW, "Sukuna Fingers", ClientCEData.getFingersConsumed() + " / 20");
        y += font.lineHeight + 6;

        graphics.fill(x, y, x + barW, y + 1, COLOR_DIVIDER);
        y += 5;

        // ---- Technique -----------------------------------------------------
        graphics.drawString(font, "Innate Technique", x, y, COLOR_HEADER, false);
        y += font.lineHeight + 3;

        String techniqueName = ClientTechniqueData.getTechniqueName();
        int techniqueColor = ClientTechniqueData.hasTechnique() ? COLOR_VALUE : COLOR_LABEL;
        graphics.drawString(font, techniqueName, x, y, techniqueColor, false);
        y += font.lineHeight + 2;

        if (ClientTechniqueData.hasTechnique()) {
            int abilityCount = ClientTechniqueData.getTechniqueAbilities().size();
            graphics.drawString(font, abilityCount + " abilities unlocked", x, y, COLOR_LABEL, false);
        }
    }

    // -------------------------------------------------------------------------

    private void drawStatRow(GuiGraphics graphics, int x, int y, int maxW,
                             String label, String value) {
        graphics.drawString(font, label, x, y, COLOR_LABEL, false);
        graphics.drawString(font, value, x + maxW - font.width(value), y, COLOR_VALUE, false);
    }
}
