package com.anastas1s12.jjs.system.shader.render.impact;

import net.minecraft.client.gui.GuiGraphics;

/**
 * Edge glow / vignette glow effect.
 */
public class EdgeGlowEffect extends ImpactEffect {
    private final int glowColor;

    public EdgeGlowEffect(int color, int durationMs) {
        super(durationMs);
        this.glowColor = color;
    }

    @Override
    public void render(GuiGraphics graphics, float partialTicks) {
        float progress = getEasedProgress();
        float intensity = 1.0f - progress;

        int width = graphics.guiWidth();
        int height = graphics.guiHeight();
        int alpha = (int)(intensity * 180);

        int color = (alpha << 24) | (glowColor & 0x00FFFFFF);

        // Top glow
        graphics.fill(0, 0, width, (int)(height * 0.15f * intensity), color);
        // Bottom glow
        graphics.fill(0, height - (int)(height * 0.15f * intensity), width, height, color);
        // Left glow
        graphics.fill(0, 0, (int)(width * 0.1f * intensity), height, color);
        // Right glow
        graphics.fill(width - (int)(width * 0.1f * intensity), 0, width, height, color);
    }
}
