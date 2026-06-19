package com.anastas1s12.jjs.system.shader.render.impact;

import net.minecraft.client.gui.GuiGraphics;

/**
 * Screen flash effect (white or black).
 */
public class FlashEffect extends ImpactEffect {
    private final float r, g, b;

    public FlashEffect(float r, float g, float b, int durationMs) {
        super(durationMs);
        this.r = r;
        this.g = g;
        this.b = b;
    }

    @Override
    public void render(GuiGraphics graphics, float partialTicks) {
        float progress = getEasedProgress();
        float alpha = (1.0f - progress);
        int color = ((int)(alpha * 255) << 24) | ((int)(r * 255) << 16) | ((int)(g * 255) << 8) | (int)(b * 255);
        graphics.fill(0, 0, graphics.guiWidth(), graphics.guiHeight(), color);
    }
}
