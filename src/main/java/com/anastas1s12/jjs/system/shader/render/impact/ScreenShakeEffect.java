package com.anastas1s12.jjs.system.shader.render.impact;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;

import java.util.Random;

/**
 * Screen shake / camera shake effect.
 */
public class ScreenShakeEffect extends ImpactEffect {
    private final float intensity;
    private final Random random = new Random();
    private float currentOffsetX = 0;
    private float currentOffsetY = 0;

    public ScreenShakeEffect(float intensity, int durationMs) {
        super(durationMs);
        this.intensity = intensity;
    }

    @Override
    public void tick() {
        super.tick();
        if (active) {
            float progress = getEasedProgress();
            float currentIntensity = intensity * (1.0f - progress);
            currentOffsetX = (random.nextFloat() - 0.5f) * currentIntensity * 2;
            currentOffsetY = (random.nextFloat() - 0.5f) * currentIntensity * 2;
        }
    }

    @Override
    public void render(GuiGraphics graphics, float partialTicks) {
        // Screen shake is applied via matrix transform in the pose stack
        graphics.pose().translate(currentOffsetX, currentOffsetY, 0);
    }
}
