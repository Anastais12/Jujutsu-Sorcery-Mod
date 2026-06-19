package com.anastas1s12.jjs.system.shader.render.impact;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.ShaderInstance;
import org.joml.Matrix4f;

/**
 * RGB channel split / chromatic aberration effect.
 */
public class RGBSplitEffect extends ImpactEffect {
    private final float intensity;

    public RGBSplitEffect(float intensity, int durationMs) {
        super(durationMs);
        this.intensity = intensity;
    }

    @Override
    public void render(GuiGraphics graphics, float partialTicks) {
        float progress = getEasedProgress();
        float currentIntensity = intensity * (1.0f - progress);

        Minecraft mc = Minecraft.getInstance();
        int width = graphics.guiWidth();
        int height = graphics.guiHeight();

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        // Draw screen with RGB offset
        float offset = currentIntensity * 20;

        // Red channel (shifted left)
        RenderSystem.setShaderColor(1.0f, 0.0f, 0.0f, 1.0f);
        graphics.fill((int)-offset, 0, width, height, 0x88FF0000);

        // Green channel (center)
        RenderSystem.setShaderColor(0.0f, 1.0f, 0.0f, 1.0f);
        graphics.fill(0, 0, width, height, 0x8800FF00);

        // Blue channel (shifted right)
        RenderSystem.setShaderColor(0.0f, 0.0f, 1.0f, 1.0f);
        graphics.fill((int)offset, 0, width, height, 0x880000FF);

        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.disableBlend();
    }
}
