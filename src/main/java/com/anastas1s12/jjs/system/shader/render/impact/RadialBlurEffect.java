package com.anastas1s12.jjs.system.shader.render.impact;

import net.minecraft.client.gui.GuiGraphics;

/**
 * Radial blur / zoom blur effect.
 */
public class RadialBlurEffect extends ImpactEffect {
    private final float strength;

    public RadialBlurEffect(float strength, int durationMs) {
        super(durationMs);
        this.strength = strength;
    }

    @Override
    public void render(GuiGraphics graphics, float partialTicks) {
        float progress = getEasedProgress();
        float currentStrength = strength * (1.0f - progress);

        // Visual representation using overlay
        int centerX = graphics.guiWidth() / 2;
        int centerY = graphics.guiHeight() / 2;
        int maxDist = Math.max(centerX, centerY);

        for (int i = 0; i < 8; i++) {
            float angle = (float)(i * Math.PI / 4);
            int offsetX = (int)(Math.cos(angle) * currentStrength * 30);
            int offsetY = (int)(Math.sin(angle) * currentStrength * 30);

            int alpha = (int)((1.0f - progress) * 30);
            graphics.fill(
                centerX + offsetX - 2, centerY + offsetY - 2,
                centerX + offsetX + 2, centerY + offsetY + 2,
                (alpha << 24) | 0xFFFFFF
            );
        }
    }
}
