package com.anastas1s12.jjs.client.screen.menu;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

/**
 * The "Domain" tab of the JJS sorcerer menu (tab index 3).
 * Uses screen_template.png (512×256) as its background.
 * Stub — implement when the Domain system is ready.
 */
public class DomainTabScreen extends AbstractMenuScreen {

    private static final int TAB_INDEX = 3;

    public DomainTabScreen() {
        super(Component.literal("Domain"), TAB_INDEX, TEX_TEMPLATE);
    }

    @Override
    protected void renderContent(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        int cx = centerX + centerW / 2;
        int cy = centerY + centerH / 2;
        String l1 = "Domain Expansion";
        String l2 = "— Coming Soon —";
        graphics.drawString(font, l1, cx - font.width(l1) / 2, cy - font.lineHeight - 2, 0xFFFF1744, false);
        graphics.drawString(font, l2, cx - font.width(l2) / 2, cy + 2, COLOR_SUBTEXT, false);
    }
}
