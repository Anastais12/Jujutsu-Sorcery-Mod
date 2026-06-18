package com.anastas1s12.jjs.client.screen.menu;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

public class StatsTabScreen extends BaseMenuScreen {

    public StatsTabScreen() {
        super(Tab.STATS);
    }

    @Override
    protected void renderContent(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        int x = contentLeft();
        int y = contentTop() + 40;

        graphics.drawCenteredString(this.font,
                Component.literal("STATS TAB"),
                x + contentWidth() / 2, y, 0xFFFFFFFF);

        graphics.drawCenteredString(this.font,
                Component.literal("Player Statistics - Coming Soon"),
                x + contentWidth() / 2, y + 30, 0xFFAAAAAA);
    }
}