package com.anastas1s12.jjs.client.screen.menu;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

public class SettingsTabScreen extends BaseMenuScreen {

    public SettingsTabScreen() {
        super(Tab.SETTINGS);
    }

    @Override
    protected void renderContent(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        int x = contentLeft();
        int y = contentTop() + 40;

        graphics.drawCenteredString(this.font,
                Component.literal("SETTINGS TAB"),
                x + contentWidth() / 2, y, 0xFFFFD700);

        graphics.drawCenteredString(this.font,
                Component.literal("Mod Settings - Coming Soon"),
                x + contentWidth() / 2, y + 30, 0xFFAAAAAA);
    }
}