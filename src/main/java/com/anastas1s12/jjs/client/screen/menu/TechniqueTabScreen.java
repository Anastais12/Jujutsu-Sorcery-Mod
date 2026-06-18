package com.anastas1s12.jjs.client.screen.menu;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

public class TechniqueTabScreen extends BaseMenuScreen {

    public TechniqueTabScreen() {
        super(Tab.TECHNIQUE);
    }

    @Override
    protected void renderContent(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        int x = contentLeft();
        int y = contentTop() + 40;

        graphics.drawCenteredString(this.font,
                Component.literal("TECHNIQUE TAB"),
                x + contentWidth() / 2, y, 0xFF42A5F5);

        graphics.drawCenteredString(this.font,
                Component.literal("Innate Technique Screen - Coming Soon"),
                x + contentWidth() / 2, y + 30, 0xFFAAAAAA);
    }
}