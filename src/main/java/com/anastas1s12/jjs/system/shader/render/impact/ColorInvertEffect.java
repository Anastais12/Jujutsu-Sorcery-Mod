package com.anastas1s12.jjs.system.shader.render.impact;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import org.lwjgl.opengl.GL11;

/**
 * Color inversion effect.
 */
public class ColorInvertEffect extends ImpactEffect {
    public ColorInvertEffect(int durationMs) {
        super(durationMs);
    }

    @Override
    public void render(GuiGraphics graphics, float partialTicks) {
        float progress = getEasedProgress();
        float intensity = 1.0f - progress;

        // Use GL logic op for color inversion
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GL11.GL_ONE_MINUS_DST_COLOR, GL11.GL_ONE_MINUS_SRC_COLOR);

        int alpha = (int)(intensity * 255);
        graphics.fill(0, 0, graphics.guiWidth(), graphics.guiHeight(), (alpha << 24) | 0xFFFFFF);

        RenderSystem.defaultBlendFunc();
        RenderSystem.disableBlend();
    }
}
