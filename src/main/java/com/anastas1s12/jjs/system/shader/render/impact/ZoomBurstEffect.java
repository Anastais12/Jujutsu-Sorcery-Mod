package com.anastas1s12.jjs.system.shader.render.impact;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import org.joml.Matrix4f;

/**
 * Zoom burst effect - rapid zoom in/out.
 */
public class ZoomBurstEffect extends ImpactEffect {
    private final float zoomAmount;

    public ZoomBurstEffect(float zoomAmount, int durationMs) {
        super(durationMs);
        this.zoomAmount = zoomAmount;
    }

    @Override
    public void render(GuiGraphics graphics, float partialTicks) {
        float progress = getEasedProgress();
        float currentZoom = 1.0f + (zoomAmount - 1.0f) * (1.0f - progress);

        int centerX = graphics.guiWidth() / 2;
        int centerY = graphics.guiHeight() / 2;

        graphics.pose().translate(centerX, centerY, 0);
        graphics.pose().scale(currentZoom, currentZoom, 1.0f);
        graphics.pose().translate(-centerX, -centerY, 0);
    }
}
