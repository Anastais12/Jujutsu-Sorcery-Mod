package com.anastas1s12.jjs.client.screen.menu;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

public class DomainTabScreen extends BaseMenuScreen {

    public DomainTabScreen() {
        super(Tab.DOMAIN);
    }

    @Override
    protected void renderContent(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        int x = contentLeft();
        int y = contentTop() + 40;

        graphics.drawCenteredString(this.font,
                Component.literal("DOMAIN TAB"),
                x + contentWidth() / 2, y, 0xFFFF1744);

        graphics.drawCenteredString(this.font,
                Component.literal("Domain Expansion - Coming Soon"),
                x + contentWidth() / 2, y + 30, 0xFFAAAAAA);
    }
}