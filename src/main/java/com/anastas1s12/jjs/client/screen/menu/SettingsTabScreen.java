package com.anastas1s12.jjs.client.screen.menu;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

/**
 * =============================================================================
 * SETTINGS TAB — Mod Settings & Configurations
 * =============================================================================
 *
 * Displays configurable settings for the mod:
 *
 *   - HUD position toggle
 *   - CE bar style (classic / minimal / hidden)
 *   - Keybind reminders
 *   - Menu animations on/off
 *   - Particle effects level
 *   - Sound volume for abilities
 *   - Domain Expansion rendering quality
 *
 * NOTE: This is a UI placeholder. Wire up to your actual config system
 * (ForgeConfigSpec or similar) for functional settings.
 *
 * =============================================================================
 */
public class SettingsTabScreen extends BaseMenuScreen {

    public SettingsTabScreen() {
        super(Tab.SETTINGS);
    }

    @Override
    protected void renderContent(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        int cx = contentLeft();
        int cy = contentTop();
        int cw = contentWidth();

        // ---- HUD SETTINGS ----
        renderSectionTitle(graphics, cx, cy, "HUD Settings");
        cy += 18;

        renderToggleRow(graphics, cx + 5, cy, "Show CE Bar", true); cy += 18;
        renderToggleRow(graphics, cx + 5, cy, "CE Bar Style", "Classic"); cy += 18;
        renderToggleRow(graphics, cx + 5, cy, "Show Mastery", true); cy += 18;
        renderToggleRow(graphics, cx + 5, cy, "Floating Damage", true); cy += 24;

        // ---- GRAPHICS SETTINGS ----
        renderSectionTitle(graphics, cx, cy, "Graphics");
        cy += 18;

        renderToggleRow(graphics, cx + 5, cy, "Menu Animations", true); cy += 18;
        renderToggleRow(graphics, cx + 5, cy, "Particle Level", "High"); cy += 18;
        renderToggleRow(graphics, cx + 5, cy, "Aura Effects", true); cy += 18;
        renderToggleRow(graphics, cx + 5, cy, "Domain Skybox", true); cy += 24;

        // ---- AUDIO SETTINGS ----
        renderSectionTitle(graphics, cx, cy, "Audio");
        cy += 18;

        renderToggleRow(graphics, cx + 5, cy, "Ability Sounds", true); cy += 18;
        renderToggleRow(graphics, cx + 5, cy, "Black Flash SFX", true); cy += 18;
        renderToggleRow(graphics, cx + 5, cy, "Domain Ambience", true); cy += 24;

        // ---- KEYBIND REMINDERS ----
        renderSectionTitle(graphics, cx, cy, "Keybinds");
        cy += 18;

        renderKeybindRow(graphics, cx + 5, cy, "Open Menu", "J"); cy += 14;
        renderKeybindRow(graphics, cx + 5, cy, "Ability Preview", "P (in menu)"); cy += 14;
        renderKeybindRow(graphics, cx + 5, cy, "Assign Hotbar", "1-9 (in menu)"); cy += 14;
        renderKeybindRow(graphics, cx + 5, cy, "Toggle RCT", "R"); cy += 14;
        renderKeybindRow(graphics, cx + 5, cy, "Use Hotbar Ability", "Mouse Wheel Click");
    }

    private void renderSectionTitle(GuiGraphics graphics, int x, int y, String title) {
        graphics.drawString(this.font,
                Component.literal(title).withStyle(ChatFormatting.BOLD),
                x + 5, y, 0xFFFFFFFF, false);
        graphics.hLine(x + 5, x + 120, y + 12, 0xFF444444);
    }

    /**
     * Renders a toggle setting row with a label and current value.
     */
    private void renderToggleRow(GuiGraphics graphics, int x, int y, String label, boolean enabled) {
        graphics.drawString(this.font, label, x, y, 0xFFCCCCCC, false);

        // Toggle button
        int btnX = x + 140;
        int btnW = 45;
        int btnColor = enabled ? 0xFF4CAF50 : 0xFFFF5252;
        String btnText = enabled ? "ON" : "OFF";

        graphics.fill(btnX, y - 1, btnX + btnW, y + 11, btnColor);
        graphics.drawString(this.font, btnText, btnX + (btnW - this.font.width(btnText)) / 2, y, 0xFFFFFFFF, false);
    }

    /**
     * Renders a toggle setting row with a string value (cycle button).
     */
    private void renderToggleRow(GuiGraphics graphics, int x, int y, String label, String value) {
        graphics.drawString(this.font, label, x, y, 0xFFCCCCCC, false);

        int valX = x + 140;
        graphics.fill(valX, y - 1, valX + 70, y + 11, 0xFF333333);
        graphics.drawString(this.font, value, valX + 4, y, 0xFFFFD700, false);
    }

    /**
     * Renders a keybind reminder row.
     */
    private void renderKeybindRow(GuiGraphics graphics, int x, int y, String action, String key) {
        graphics.drawString(this.font, action, x, y, 0xFFAAAAAA, false);
        int keyW = this.font.width(key);
        graphics.drawString(this.font,
                Component.literal(key).withStyle(ChatFormatting.YELLOW),
                x + 130 - keyW, y, 0xFFFFD700, false);
    }
}
