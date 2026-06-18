package com.anastas1s12.jjs.client.screen.menu;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

public class InventoryTabScreen extends BaseMenuScreen {

    public InventoryTabScreen() {
        super(Tab.INVENTORY);
    }

    @Override
    protected void renderContent(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        int x = contentLeft();
        int y = contentTop() + 40;

        graphics.drawCenteredString(this.font,
                Component.literal("INVENTORY TAB"),
                x + contentWidth() / 2, y, 0xFFFFD700);

        graphics.drawCenteredString(this.font,
                Component.literal("Cursed Tools & Inventory - Coming Soon"),
                x + contentWidth() / 2, y + 30, 0xFFAAAAAA);
    }
}