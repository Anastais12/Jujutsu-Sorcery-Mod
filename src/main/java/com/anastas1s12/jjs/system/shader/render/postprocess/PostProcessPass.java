package com.anastas1s12.jjs.system.shader.render.postprocess;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import org.joml.Matrix4f;

/**
 * Individual post-processing pass in the pipeline.
 */
public abstract class PostProcessPass {
    protected final String name;
    protected boolean enabled = true;
    protected int width, height;

    protected static final BufferBuilder FULLSCREEN_BUFFER = Tesselator.getInstance().getBuilder();

    public PostProcessPass(String name) {
        this.name = name;
        Minecraft mc = Minecraft.getInstance();
        this.width = mc.getWindow().getWidth();
        this.height = mc.getWindow().getHeight();
    }

    public abstract void apply(RenderTarget input, RenderTarget output);

    public abstract void tick();

    public void resize(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public String getName() {
        return name;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * Render a fullscreen quad with the given shader.
     */
    protected void renderFullscreenQuad(RenderTarget output, int shaderId) {
        output.bindWrite(true);
        RenderSystem.setShader(() -> GameRenderer.getPositionTexShader());

        float u0 = 0, u1 = 1, v0 = 0, v1 = 1;

        BufferBuilder buffer = Tesselator.getInstance().getBuilder();
        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        buffer.vertex(0, 0, 0).uv(u0, v1).endVertex();
        buffer.vertex(width, 0, 0).uv(u1, v1).endVertex();
        buffer.vertex(width, height, 0).uv(u1, v0).endVertex();
        buffer.vertex(0, height, 0).uv(u0, v0).endVertex();
        BufferUploader.drawWithShader(buffer.end());
    }
}