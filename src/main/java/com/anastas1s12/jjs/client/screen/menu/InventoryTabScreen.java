package com.anastas1s12.jjs.client.screen.menu;

import com.anastas1s12.jjs.JujutsuSorcery;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.resources.ResourceLocation;

/**
 * =============================================================================
 * INVENTORY TAB — Cursed Tools & Equipment
 * =============================================================================
 *
 * Displays the player's cursed tools and equipment:
 *
 *   TOP: Equipment slots (main hand, offhand, armor)
 *   MIDDLE: Cursed tool inventory grid
 *   BOTTOM: Tool details when selected (grade, CE bonuses, ability)
 *
 * This is a simplified version. For a full implementation, you would
 * integrate with the player's actual inventory and create custom
 * CursedToolItem instances with grade/rarity/bonuses.
 *
 * =============================================================================
 */
public class InventoryTabScreen extends BaseMenuScreen {

    /** Cursed tool slot texture */
    public static final ResourceLocation TOOL_SLOT =
            ResourceLocation.fromNamespaceAndPath(JujutsuSorcery.MOD_ID, "textures/gui/menu/tool_slot.png");

    public InventoryTabScreen() {
        super(Tab.INVENTORY);
    }

    @Override
    protected void renderContent(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        int cx = contentLeft();
        int cy = contentTop();
        int cw = contentWidth();

        // ---- EQUIPPED TOOLS ----
        renderEquippedSection(graphics, cx, cy, cw, mouseX, mouseY);
        cy += 70;

        // ---- TOOL INVENTORY GRID ----
        renderToolGrid(graphics, cx, cy, cw, mouseX, mouseY);
        cy += 100;

        // ---- TOOL DETAILS (if selected) ----
        renderToolDetails(graphics, cx, cy, cw);
    }

    private void renderEquippedSection(GuiGraphics graphics, int x, int y, int width, int mouseX, int mouseY) {
        graphics.drawString(this.font, "Equipped", x + 5, y, 0xFFFFFFFF, false);
        y += 14;

        // Main hand, Offhand, 4 armor slots
        String[] slots = {"Main Hand", "Offhand", "Head", "Chest", "Legs", "Feet"};
        int slotSize = 32;
        int startX = x + 5;

        for (int i = 0; i < slots.length; i++) {
            int slotX = startX + i * (slotSize + 5);
            boolean hovered = mouseX >= slotX && mouseX < slotX + slotSize
                    && mouseY >= y && mouseY < y + slotSize;

            int bgColor = hovered ? 0xFF3A3A3A : 0xFF222222;
            graphics.fill(slotX, y, slotX + slotSize, y + slotSize, bgColor);
            graphics.renderOutline(slotX, y, slotSize, slotSize, hovered ? 0xFFFFFFFF : 0xFF555555);

            // Slot label (small, below)
            int labelW = this.font.width(slots[i]);
            graphics.drawString(this.font, slots[i],
                    slotX + (slotSize - labelW) / 2, y + slotSize + 2, 0xFF888888, false);

            if (hovered) {
                tooltipText = slots[i] + " slot";
                tooltipX = mouseX;
                tooltipY = mouseY - 15;
            }
        }

        graphics.hLine(x, x + width, y + slotSize + 16, 0xFF444444);
    }

    private void renderToolGrid(GuiGraphics graphics, int x, int y, int width, int mouseX, int mouseY) {
        graphics.drawString(this.font, "Cursed Tools", x + 5, y, 0xFFFFFFFF, false);
        y += 14;

        int slotSize = 28;
        int cols = 6;
        int rows = 3;

        // Example tool data
        String[][] tools = {
                {"Split Soul Katana", "Special", "SLASHING"},
                {"Inverted Spear", "Special", "PIERCING"},
                {"Black Rope", "Grade 1", "PHYSICAL"},
                {"Slaughter Demon", "Grade 2", "SLASHING"},
                {null, null, null},
                {null, null, null},
        };

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                int idx = row * cols + col;
                int slotX = x + 5 + col * (slotSize + 4);
                int slotY = y + row * (slotSize + 4);
                boolean hovered = mouseX >= slotX && mouseX < slotX + slotSize
                        && mouseY >= slotY && mouseY < slotY + slotSize;

                int bgColor = hovered ? 0xFF3A3A3A : 0xFF222222;
                graphics.fill(slotX, slotY, slotX + slotSize, slotY + slotSize, bgColor);
                graphics.renderOutline(slotX, slotY, slotSize, slotSize,
                        hovered ? 0xFFFFFFFF : 0xFF555555);

                if (idx < tools.length && tools[idx][0] != null) {
                    String name = tools[idx][0];
                    String grade = tools[idx][1];

                    // Tool icon placeholder (colored by grade)
                    int gradeColor = switch (grade) {
                        case "Special" -> 0xFFFFD700;
                        case "Grade 1" -> 0xFF00BCD4;
                        case "Grade 2" -> 0xFF4CAF50;
                        default -> 0xFFAAAAAA;
                    };

                    graphics.fill(slotX + 3, slotY + 3, slotX + slotSize - 3, slotY + slotSize - 3,
                            gradeColor & 0xFF444444);

                    if (hovered) {
                        tooltipText = name + " (" + grade + ")";
                        tooltipX = mouseX;
                        tooltipY = mouseY - 15;
                    }
                }
            }
        }
    }

    private void renderToolDetails(GuiGraphics graphics, int x, int y, int width) {
        graphics.hLine(x, x + width, y, 0xFF444444);
        y += 8;

        graphics.drawString(this.font, "Tool Details", x + 5, y, 0xFFFFFFFF, false);
        y += 14;

        // Placeholder for when a tool is selected
        graphics.drawString(this.font,
                Component.literal("Select a tool to view details").withStyle(ChatFormatting.ITALIC),
                x + 10, y, 0xFF666666, false);
    }
}
