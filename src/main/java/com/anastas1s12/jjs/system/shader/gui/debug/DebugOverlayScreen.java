package com.anastas1s12.jjs.system.shader.gui.debug;

import com.anastas1s12.jjs.JujutsuSorcery;
import com.anastas1s12.jjs.system.shader.render.impact.ImpactEffectManager;
import com.anastas1s12.jjs.system.shader.render.postprocess.PostProcessPipeline;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

/**
 * Debug Overlay (F9)
 *
 * Displays:
 * - Current active shader
 * - Active passes
 * - GPU frame time
 * - FPS
 * - Shader compile status
 * - Active uniforms
 * - Impact effects currently running
 */
public class DebugOverlayScreen extends Screen {

    private final Minecraft mc = Minecraft.getInstance();
    private long lastFrameTime = 0;
    private final List<Long> frameTimes = new ArrayList<>();
    private static final int FRAME_SAMPLE_SIZE = 120;

    public DebugOverlayScreen() {
        super(Component.literal("Shader Debug"));
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        graphics.fill(0, 0, this.width, this.height, 0x88000000);

        int x = 10;
        int y = 10;
        int lineHeight = 12;
        int color = 0xFF00FF00;
        int labelColor = 0xFFAAAAAA;

        graphics.drawString(this.font, "§l§nShader Workbench Debug Overlay", x, y, 0xFFFFFF);
        y += lineHeight + 4;

        float fps = mc.getFps();
        graphics.drawString(this.font, "FPS: " + String.format("%.1f", fps), x, y, color);
        y += lineHeight;

        float avgFrameTime = getAverageFrameTime();
        graphics.drawString(this.font, "Frame Time: " + String.format("%.2f ms", avgFrameTime), x, y, color);
        y += lineHeight;

        String vendor = GL11.glGetString(GL11.GL_VENDOR);
        String renderer = GL11.glGetString(GL11.GL_RENDERER);
        graphics.drawString(this.font, "GPU: " + renderer, x, y, labelColor);
        y += lineHeight;
        graphics.drawString(this.font, "Vendor: " + vendor, x, y, labelColor);
        y += lineHeight;

        y += 4;
        graphics.drawString(this.font, "§l§nActive Shaders:", x, y, 0xFFFFFF);
        y += lineHeight;

        var shaderNames = JujutsuSorcery.getInstance().getRenderManager().getLoadedShaderNames();
        if (shaderNames.isEmpty()) {
            graphics.drawString(this.font, "  (none loaded)", x, y, 0xFF888888);
            y += lineHeight;
        } else {
            for (String name : shaderNames) {
                var shader = JujutsuSorcery.getInstance().getRenderManager().getShader(name);
                String status = shader != null && shader.isActive() ? "§a[ON]" : "§7[OFF]";
                graphics.drawString(this.font, "  " + status + " §f" + name, x, y, 0xFFFFFF);
                y += lineHeight;
            }
        }

        y += 4;
        graphics.drawString(this.font, "§l§nPipeline Passes:", x, y, 0xFFFFFF);
        y += lineHeight;

        PostProcessPipeline pipeline = JujutsuSorcery.getInstance().getPostProcessPipeline();
        if (pipeline != null) {
            var passes = pipeline.getPasses();
            if (passes.isEmpty()) {
                graphics.drawString(this.font, "  (no passes)", x, y, 0xFF888888);
                y += lineHeight;
            } else {
                for (var pass : passes) {
                    String status = pass.isEnabled() ? "§a[ON]" : "§7[OFF]";
                    graphics.drawString(this.font, "  " + status + " §f" + pass.getName(), x, y, 0xFFFFFF);
                    y += lineHeight;
                }
            }
        }

        y += 4;
        graphics.drawString(this.font, "§l§nImpact Effects:", x, y, 0xFFFFFF);
        y += lineHeight;

        ImpactEffectManager impactManager = JujutsuSorcery.getInstance().getImpactEffectManager();
        if (impactManager != null) {
            var activeEffects = impactManager.getActiveEffects();
            if (activeEffects.isEmpty()) {
                graphics.drawString(this.font, "  (none running)", x, y, 0xFF888888);
                y += lineHeight;
            } else {
                graphics.drawString(this.font, "  Active: " + activeEffects.size(), x, y, color);
                y += lineHeight;
            }
        }

        y += 4;
        graphics.drawString(this.font, "§l§nUniforms (current shader):", x, y, 0xFFFFFF);
        y += lineHeight;

        for (String name : shaderNames) {
            var shader = JujutsuSorcery.getInstance().getRenderManager().getShader(name);
            if (shader != null && shader.isActive()) {
                var uniforms = shader.getUniforms();
                int count = 0;
                for (var entry : uniforms.entrySet()) {
                    graphics.drawString(this.font,
                            "  " + entry.getKey() + " (loc=" + entry.getValue().location + ")",
                            x, y, 0xFFCCCCCC);
                    y += lineHeight;
                    if (++count >= 8) {
                        graphics.drawString(this.font, "  ... and " + (uniforms.size() - 8) + " more",
                                x, y, 0xFF888888);
                        y += lineHeight;
                        break;
                    }
                }
                break;
            }
        }

        long now = System.nanoTime();
        if (lastFrameTime != 0) {
            frameTimes.add(now - lastFrameTime);
            if (frameTimes.size() > FRAME_SAMPLE_SIZE) {
                frameTimes.remove(0);
            }
        }
        lastFrameTime = now;
    }

    private float getAverageFrameTime() {
        if (frameTimes.isEmpty()) return 0f;
        long sum = 0;
        for (long t : frameTimes) sum += t;
        return (sum / frameTimes.size()) / 1_000_000.0f;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 67) { // F9 toggle
            mc.setScreen(null);
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }
}
