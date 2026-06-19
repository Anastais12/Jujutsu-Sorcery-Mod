package com.anastas1s12.jjs.system.shader.render.impact;

import net.minecraft.client.gui.GuiGraphics;

/**
 * Motion blur effect.
 */
public class MotionBlurEffect extends ImpactEffect {
    private final float strength;

    public MotionBlurEffect(float strength, int durationMs) {
        super(durationMs);
        this.strength = strength;
    }

    @Override
    public void render(GuiGraphics graphics, float partialTicks) {
        float progress = getEasedProgress();
        float alpha = (1.0f - progress) * strength * 0.5f;

        int width = graphics.guiWidth();
        int height = graphics.guiHeight();

        // Simple directional blur simulation
        for (int i = 1; i <= 5; i++) {
            int offset = (int)(i * alpha * 10);
            int a = (int)(alpha * 255 / i);
            graphics.fill(offset, 0, width + offset, height, (a << 24));
        }
    }
}
