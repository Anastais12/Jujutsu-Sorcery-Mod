package com.anastas1s12.jjs.client.screen.menu;

import com.anastas1s12.jjs.JujutsuSorcery;
import com.anastas1s12.jjs.client.ClientCEData;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;

/**
 * =============================================================================
 * TECHNIQUE TAB — Innate Technique Information
 * =============================================================================
 *
 * Displays the player's innate technique (their "class"):
 *
 *   TOP:
 *     - Technique name (large, e.g., "Limitless")
 *     - Technique description
 *     - Technique icon (large)
 *
 *   MIDDLE:
 *     - Inherited trait description
 *     - List of all abilities in this technique (with unlock status)
 *     - Technique passive effects
 *
 *   BOTTOM:
 *     - Technique lore / flavor text
 *     - Comparison to other techniques (optional)
 *
 * NOTE: This is a placeholder screen. You'll want to replace the
 * hardcoded "Limitless" data with dynamic technique loading from
 * your player's capability or a technique registry.
 *
 * =============================================================================
 */
public class TechniqueTabScreen extends BaseMenuScreen {

    // ---- Placeholder technique data — replace with actual system ----
    private String techniqueName = "Limitless";
    private String techniqueDesc = "The apex of jujutsu sorcery. Manipulates space at the atomic level.";
    private String[] passives = {
            "Infinity: Automatically nullifies projectiles",
            "Cursed Energy: +50% efficiency with Six Eyes",
            "Spatial Manipulation: Unlocks Blue and Red"
    };

    public TechniqueTabScreen() {
        super(Tab.TECHNIQUE);
    }

    @Override
    protected void renderContent(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        int cx = contentLeft();
        int cy = contentTop();
        int cw = contentWidth();

        // ---- TECHNIQUE HEADER ----
        renderTechniqueHeader(graphics, cx, cy, cw);
        cy += 80;

        // ---- PASSIVE EFFECTS ----
        renderPassiveEffects(graphics, cx, cy, cw);
        cy += passives.length * 18 + 30;

        // ---- ABILITY LIST IN THIS TECHNIQUE ----
        renderTechniqueAbilities(graphics, cx, cy, cw);
        cy += 80;

        // ---- LORE / FLAVOR TEXT ----
        renderLore(graphics, cx, cy, cw);
    }

    private void renderTechniqueHeader(GuiGraphics graphics, int x, int y, int width) {
        // Large technique icon placeholder (left side)
        graphics.fill(x + 10, y, x + 60, y + 50, 0xFF1565C0);
        graphics.renderOutline(x + 10, y, 50, 50, 0xFF42A5F5);
        graphics.drawCenteredString(this.font, "\u221E", x + 35, y + 18, 0xFFFFFFFF);

        // Technique name
        int nameX = x + 70;
        graphics.drawString(this.font,
                Component.literal("Innate Technique").withStyle(ChatFormatting.GRAY),
                nameX, y, 0xFFAAAAAA, false);

        graphics.drawString(this.font,
                Component.literal(techniqueName).withStyle(ChatFormatting.BOLD, ChatFormatting.AQUA),
                nameX, y + 12, 0xFF42A5F5, false);

        // Description (word-wrapped)
        y += 32;
        String[] words = techniqueDesc.split(" ");
        StringBuilder line = new StringBuilder();
        for (String word : words) {
            String test = line.length() > 0 ? line + " " + word : word;
            if (this.font.width(test) > width - 80 && line.length() > 0) {
                graphics.drawString(this.font, line.toString(), nameX, y, 0xFFCCCCCC, false);
                y += 12;
                line = new StringBuilder(word);
            } else {
                line = new StringBuilder(test);
            }
        }
        if (line.length() > 0) {
            graphics.drawString(this.font, line.toString(), nameX, y, 0xFFCCCCCC, false);
        }

        // Divider
        graphics.hLine(x, x + width, y + 16, 0xFF444444);
    }

    private void renderPassiveEffects(GuiGraphics graphics, int x, int y, int width) {
        graphics.drawString(this.font, "Passive Effects", x + 5, y, 0xFFFFFFFF, false);
        y += 14;

        boolean sixEyes = ClientCEData.hasSixEyes();

        for (int i = 0; i < passives.length; i++) {
            String passive = passives[i];
            boolean active = i == 0 || (i == 1 && sixEyes) || i == 2;

            // Bullet point
            graphics.drawString(this.font, active ? "\u25CF" : "\u25CB",
                    x + 10, y, active ? 0xFF4CAF50 : 0xFF666666, false);

            // Passive text
            graphics.drawString(this.font, passive, x + 22, y,
                    active ? 0xFFCCCCCC : 0xFF666666, false);

            y += 18;
        }
    }

    private void renderTechniqueAbilities(GuiGraphics graphics, int x, int y, int width) {
        graphics.drawString(this.font, "Technique Abilities", x + 5, y, 0xFFFFFFFF, false);
        y += 14;

        // Show abilities grouped by type with colored indicators
        String[][] abilities = {
                {"Cursed Punch", "BASIC", "Unlocked"},
                {"Divergent Fist", "BASIC", "Unlocked"},
                {"Black Flash", "BASIC", "Unlocked"},
                {"Limitless: Blue", "ADVANCED", "Unlocked"},
                {"Limitless: Red", "ADVANCED", "Mastery 25"},
                {"Hollow Purple", "SPECIAL", "Mastery 100"},
                {"Infinite Void", "DOMAIN", "Mastery 50"},
        };

        for (String[] ability : abilities) {
            String name = ability[0];
            String type = ability[1];
            String status = ability[2];

            int typeColor = switch (type) {
                case "BASIC" -> 0xFF4CAF50;
                case "ADVANCED" -> 0xFF00BCD4;
                case "SPECIAL" -> 0xFFE040FB;
                case "DOMAIN" -> 0xFFFF1744;
                default -> 0xFFAAAAAA;
            };

            boolean unlocked = status.equals("Unlocked");

            // Type color indicator
            graphics.fill(x + 10, y + 2, x + 14, y + 10, typeColor);

            // Name
            graphics.drawString(this.font, name, x + 18, y,
                    unlocked ? 0xFFFFFFFF : 0xFF666666, false);

            // Status (right aligned)
            int statusColor = unlocked ? 0xFF4CAF50 : 0xFFFFA000;
            int statusW = this.font.width(status);
            graphics.drawString(this.font, status, x + width - 15 - statusW, y, statusColor, false);

            y += 14;
        }
    }

    private void renderLore(GuiGraphics graphics, int x, int y, int width) {
        graphics.hLine(x, x + width, y, 0xFF444444);
        y += 8;

        String lore = "Those who inherit the Limitless possess the power to manipulate space itself. " +
                "With the Six Eyes, one can achieve perfect energy efficiency, making the impossible possible.";

        graphics.drawString(this.font, "Lore", x + 5, y, 0xFFFFD700, false);
        y += 14;

        // Word-wrap lore
        String[] words = lore.split(" ");
        StringBuilder line = new StringBuilder();
        for (String word : words) {
            String test = line.length() > 0 ? line + " " + word : word;
            if (this.font.width(test) > width - 20 && line.length() > 0) {
                graphics.drawString(this.font, line.toString(), x + 10, y, 0xFF999999, false);
                y += 12;
                line = new StringBuilder(word);
            } else {
                line = new StringBuilder(test);
            }
        }
        if (line.length() > 0) {
            graphics.drawString(this.font, line.toString(), x + 10, y, 0xFF999999, false);
        }
    }
}
