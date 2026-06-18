package com.anastas1s12.jjs.client.screen.menu;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

public class CursedEnergyTabScreen extends BaseMenuScreen {

    public CursedEnergyTabScreen() {
        super(Tab.CURSED_ENERGY);
    }

    @Override
    protected void renderContent(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        int x = contentLeft();
        int y = contentTop() + 40;

        graphics.drawCenteredString(this.font,
                Component.literal("CURSED ENERGY TAB"),
                x + contentWidth() / 2, y, 0xFF00E676);

        graphics.drawCenteredString(this.font,
                Component.literal("Cursed Energy Details - Coming Soon"),
                x + contentWidth() / 2, y + 30, 0xFFAAAAAA);
    }
}