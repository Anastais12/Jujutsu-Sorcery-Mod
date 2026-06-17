package com.anastas1s12.jjs.client.screen.menu;

import com.anastas1s12.jjs.ability.Grade;
import com.anastas1s12.jjs.client.ClientCEData;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;

/**
 * =============================================================================
 * MASTERY TAB — Mastery Progression & Rewards
 * =============================================================================
 *
 * Displays the player's mastery progression:
 *
 *   TOP:
 *     - Current grade with large icon
 * *     - Mastery level (large number)
 *     - XP progress bar toward next level
 *     - Total mastery points available to spend
 *
 *   MIDDLE:
 *     - Grade progression path (Grade 4 -> Special Grade)
 *     - Key milestones at levels 10, 25, 50, 75, 100
 *     - Rewards unlocked at each milestone
 *
 *   BOTTOM:
 *     - Recent activity log (kills, training, etc.)
 *     - XP sources breakdown
 *
 * =============================================================================
 */
public class MasteryTabScreen extends BaseMenuScreen {

    public MasteryTabScreen() {
        super(Tab.MASTERY);
    }

    @Override
    protected void renderContent(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        int cx = contentLeft();
        int cy = contentTop();
        int cw = contentWidth();

        // ---- MASTERY HEADER ----
        renderMasteryHeader(graphics, cx, cy, cw);
        cy += 75;

        // ---- GRADE PROGRESSION PATH ----
        renderGradePath(graphics, cx, cy, cw);
        cy += 70;

        // ---- MILESTONES ----
        renderMilestones(graphics, cx, cy, cw);
        cy += 90;

        // ---- MASTERY POINTS ----
        renderMasteryPoints(graphics, cx, cy, cw);
    }

    private void renderMasteryHeader(GuiGraphics graphics, int x, int y, int width) {
        Grade grade = getPlayerGrade();
        int mastery = ClientCEData.getMasteryLevel();
        int xp = ClientCEData.getMasteryXP();
        int xpNeeded = com.anastas1s12.jjs.capability.CursedEnergy.MASTERY_XP_PER_LEVEL;
        float progress = (float) xp / xpNeeded;

        // Large grade icon placeholder (left)
        graphics.fill(x + 10, y, x + 55, y + 45, grade.getColor() & 0xFF555555);
        graphics.renderOutline(x + 10, y, 45, 45, grade.getColor());
        graphics.drawCenteredString(this.font,
                grade.name().substring(0, 1), x + 32, y + 15, grade.getColor());

        // Grade title
        int tx = x + 65;
        graphics.drawString(this.font,
                Component.literal(grade.getTitle()).withStyle(grade.getChatColor()),
                tx, y, grade.getColor(), false);

        // Mastery level (big number)
        graphics.drawString(this.font,
                Component.literal("Lv. " + mastery).withStyle(ChatFormatting.BOLD),
                tx, y + 14, 0xFFFFFFFF, false);

        // XP bar
        renderProgressBar(graphics, tx, y + 32, width - 75, 10,
                progress,
                String.format("%d / %d XP", xp, xpNeeded),
                0xFFFFD700, 0xFF333333);

        // Divider
        graphics.hLine(x, x + width, y + 52, 0xFF444444);
    }

    private void renderGradePath(GuiGraphics graphics, int x, int y, int width) {
        graphics.drawString(this.font, "Grade Progression", x + 5, y, 0xFFFFFFFF, false);
        y += 16;

        Grade[] grades = Grade.values();
        int total = grades.length;
        int spacing = (width - 30) / (total - 1);
        int mastery = ClientCEData.getMasteryLevel();

        // Draw connection line
        graphics.hLine(x + 20, x + 20 + spacing * (total - 1), y + 15, 0xFF444444);

        // Active portion of line
        int activeGrades = 0;
        for (int i = 0; i < total; i++) {
            if (mastery >= grades[i].getMinMastery()) activeGrades = i;
        }
        int activeWidth = spacing * activeGrades;
        if (activeWidth > 0) {
            graphics.hLine(x + 20, x + 20 + activeWidth, y + 15, 0xFF00E676);
        }

        // Grade nodes
        for (int i = 0; i < total; i++) {
            Grade g = grades[i];
            int nodeX = x + 20 + i * spacing;
            boolean achieved = mastery >= g.getMinMastery();
            boolean current = getPlayerGrade() == g;

            int nodeColor = achieved ? g.getColor() : 0xFF555555;
            int nodeSize = current ? 14 : 10;

            // Node circle
            graphics.fill(nodeX - nodeSize / 2, y + 15 - nodeSize / 2,
                    nodeX + nodeSize / 2, y + 15 + nodeSize / 2, nodeColor);

            // Grade label below
            String label = g.name().replace("GRADE_", "G");
            int labelW = this.font.width(label);
            graphics.drawString(this.font, label,
                    nodeX - labelW / 2, y + 24,
                    achieved ? g.getColor() : 0xFF666666, false);

            // Mastery requirement below label
            String req = "(" + g.getMinMastery() + ")";
            int reqW = this.font.width(req);
            graphics.drawString(this.font, req,
                    nodeX - reqW / 2, y + 36, 0xFF666666, false);
        }
    }

    private void renderMilestones(GuiGraphics graphics, int x, int y, int width) {
        graphics.drawString(this.font, "Milestones", x + 5, y, 0xFFFFFFFF, false);
        y += 14;

        String[][] milestones = {
                {"10", "Divergent Fist", "Unlock first advanced move"},
                {"25", "Cursed Tool Use", "Equip and use cursed tools"},
                {"50", "RCT Unlock", "Reverse Cursed Technique"},
                {"75", "Advanced RCT", "Heal others with RCT"},
                {"100", "Special Grade", "Ultimate techniques unlocked"},
        };

        for (String[] m : milestones) {
            int req = Integer.parseInt(m[0]);
            String name = m[1];
            String desc = m[2];
            boolean achieved = ClientCEData.getMasteryLevel() >= req;

            int color = achieved ? 0xFF4CAF50 : 0xFF555555;

            // Level badge
            graphics.fill(x + 10, y, x + 30, y + 14, color);
            graphics.drawString(this.font, m[0], x + 13, y + 3, 0xFF000000, false);

            // Name and desc
            graphics.drawString(this.font, name, x + 35, y, color, false);
            graphics.drawString(this.font, desc, x + 35, y + 10, 0xFF888888, false);

            y += 22;
        }
    }

    private void renderMasteryPoints(GuiGraphics graphics, int x, int y, int width) {
        graphics.hLine(x, x + width, y, 0xFF444444);
        y += 8;

        int points = ClientCEData.getMasteryPoints();
        graphics.drawString(this.font,
                Component.literal("Available Points: " + points).withStyle(ChatFormatting.GOLD),
                x + 5, y, 0xFFFFD700, false);

        String hint = points > 0 ? "Spend points in the Abilities tab!" : "Earn XP to gain more points.";
        graphics.drawString(this.font, hint, x + 5, y + 14, 0xFFAAAAAA, false);
    }
}
