package com.anastas1s12.jjs.system.shader.render.postprocess;

import com.anastas1s12.jjs.JujutsuSorcery;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.pipeline.TextureTarget;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import org.lwjgl.opengl.GL30;

import java.util.ArrayList;
import java.util.List;

/**
 * Post-processing pipeline implementing multi-pass shader effects.
 *
 * Pipeline: World Render → Effect 1 → Effect 2 → ... → Final Screen
 */
public class PostProcessPipeline {
    private final Minecraft mc = Minecraft.getInstance();
    private final List<PostProcessPass> passes = new ArrayList<>();
    private final List<PostProcessPass> activePasses = new ArrayList<>();

    private RenderTarget inputTarget;
    private RenderTarget outputTarget;
    private boolean enabled = true;
    private boolean needsCapture = false;

    public PostProcessPipeline() {
        initializeTargets();
    }

    private void initializeTargets() {
        int width = mc.getWindow().getWidth();
        int height = mc.getWindow().getHeight();
        inputTarget = new TextureTarget(width, height, true, Minecraft.ON_OSX);
        outputTarget = new TextureTarget(width, height, true, Minecraft.ON_OSX);
    }

    public void addPass(PostProcessPass pass) {
        passes.add(pass);
        if (pass.isEnabled()) {
            activePasses.add(pass);
        }
    }

    public void removePass(String name) {
        passes.removeIf(p -> p.getName().equals(name));
        activePasses.removeIf(p -> p.getName().equals(name));
    }

    public void enableShader(String name) {
        for (PostProcessPass pass : passes) {
            if (pass.getName().equals(name)) {
                pass.setEnabled(true);
                if (!activePasses.contains(pass)) {
                    activePasses.add(pass);
                }
                JujutsuSorcery.LOGGER.info("Enabled post-process pass: {}", name);
                return;
            }
        }
    }

    public void disableShader(String name) {
        for (PostProcessPass pass : passes) {
            if (pass.getName().equals(name)) {
                pass.setEnabled(false);
                activePasses.remove(pass);
                JujutsuSorcery.LOGGER.info("Disabled post-process pass: {}", name);
                return;
            }
        }
    }

    public void captureFrame() {
        needsCapture = true;
    }

    /**
     * Apply all active post-processing passes.
     */
    public void apply() {
        if (!enabled || activePasses.isEmpty()) return;

        if (needsCapture) {
            copyMainFramebuffer();
            needsCapture = false;
        }

        RenderTarget source = inputTarget;
        RenderTarget dest = outputTarget;

        for (PostProcessPass pass : activePasses) {
            pass.apply(source, dest);
            // Swap buffers
            RenderTarget temp = source;
            source = dest;
            dest = temp;
        }

        // Final blit to screen
        if (source != inputTarget) {
            blitToScreen(source);
        }
    }

    private void copyMainFramebuffer() {
        mc.getMainRenderTarget().bindWrite(false);
        inputTarget.copyDepthFrom(mc.getMainRenderTarget());
        GlStateManager._glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, mc.getMainRenderTarget().frameBufferId);
        GlStateManager._glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, inputTarget.frameBufferId);
        GL30.glBlitFramebuffer(0, 0, inputTarget.width, inputTarget.height,
                0, 0, inputTarget.width, inputTarget.height,
                GL30.GL_COLOR_BUFFER_BIT, GL30.GL_LINEAR);
    }

    private void blitToScreen(RenderTarget source) {
        mc.getMainRenderTarget().bindWrite(false);
        GlStateManager._glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, source.frameBufferId);
        GlStateManager._glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, mc.getMainRenderTarget().frameBufferId);
        GL30.glBlitFramebuffer(0, 0, source.width, source.height,
                0, 0, mc.getMainRenderTarget().width, mc.getMainRenderTarget().height,
                GL30.GL_COLOR_BUFFER_BIT, GL30.GL_LINEAR);
    }

    public void tick() {
        // Update dynamic uniforms, animations
        for (PostProcessPass pass : activePasses) {
            pass.tick();
        }
    }

    public void resize(int width, int height) {
        inputTarget.resize(width, height, Minecraft.ON_OSX);
        outputTarget.resize(width, height, Minecraft.ON_OSX);
        for (PostProcessPass pass : passes) {
            pass.resize(width, height);
        }
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public List<PostProcessPass> getPasses() {
        return new ArrayList<>(passes);
    }

    public void reset() {
        activePasses.clear();
        for (PostProcessPass pass : passes) {
            pass.setEnabled(false);
        }
    }
}