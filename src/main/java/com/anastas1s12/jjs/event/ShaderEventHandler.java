package com.anastas1s12.jjs.event;

import com.anastas1s12.jjs.JujutsuSorcery;
import com.anastas1s12.jjs.system.shader.render.ShaderRenderManager;
import com.anastas1s12.jjs.system.shader.render.impact.ImpactEffectManager;
import com.anastas1s12.jjs.system.shader.render.postprocess.PostProcessPipeline;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * ShaderWorkbench Event Handler
 *
 * Handles all Forge events for the mod:
 * - Render events for shader pipeline injection
 * - Tick events for impact frame updates
 * - Combat events for impact frame triggers
 * - GUI render events for debug overlay
 */
@Mod.EventBusSubscriber(modid = JujutsuSorcery.MOD_ID, value = Dist.CLIENT)
public class ShaderEventHandler {

    private final Minecraft mc = Minecraft.getInstance();
    private long lastFrameTime = 0;
    private int frameCount = 0;
    private float fps = 0;

    /**
     * Client tick event - updates impact effects and shader pipeline
     */
    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        if (mc.level == null) return;

        // Update impact effects
        ImpactEffectManager impactManager = JujutsuSorcery.getInstance().getImpactEffectManager();
        if (impactManager != null) {
            impactManager.tick();
        }

        // Update shader pipeline
        PostProcessPipeline pipeline = JujutsuSorcery.getInstance().getPostProcessPipeline();
        if (pipeline != null) {
            pipeline.tick();
        }
    }

    /**
     * Render tick event - updates render-time uniforms
     */
    @SubscribeEvent
    public void onRenderTick(TickEvent.RenderTickEvent event) {
        if (event.phase != TickEvent.Phase.START) return;

        ShaderRenderManager renderManager = JujutsuSorcery.getInstance().getRenderManager();
        if (renderManager != null) {
            renderManager.updateUniforms(event.renderTickTime);
        }

        // Calculate FPS for debug overlay
        frameCount++;
        long currentTime = System.nanoTime();
        if (currentTime - lastFrameTime >= 1_000_000_000L) {
            fps = frameCount;
            frameCount = 0;
            lastFrameTime = currentTime;
        }
    }

    /**
     * Post-world render - applies post-processing shaders
     */
    @SubscribeEvent
    public void onRenderLevelStage(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_LEVEL) return;

        PostProcessPipeline pipeline = JujutsuSorcery.getInstance().getPostProcessPipeline();
        if (pipeline != null && pipeline.isEnabled()) {
            // Capture the current framebuffer for post-processing
            pipeline.captureFrame();
        }
    }

    /**
     * GUI overlay render - applies final screen effects and debug info
     */
    @SubscribeEvent
    public void onRenderGuiOverlay(RenderGuiOverlayEvent.Post event) {
        // Apply impact frame effects over the GUI
        ImpactEffectManager impactManager = JujutsuSorcery.getInstance().getImpactEffectManager();
        if (impactManager != null) {
            impactManager.render(event.getGuiGraphics(), event.getPartialTick());
        }
    }

    /**
     * Player attack event - trigger impact effects
     */
    @SubscribeEvent
    public void onPlayerAttack(AttackEntityEvent event) {
        if (!event.getEntity().level().isClientSide()) return;

        ImpactEffectManager impactManager = JujutsuSorcery.getInstance().getImpactEffectManager();
        if (impactManager != null) {
            impactManager.onPlayerAttack(event.getEntity(), event.getTarget());
        }
    }

    /**
     * Entity death event - trigger impact effects
     */
    @SubscribeEvent
    public void onLivingDeath(LivingDeathEvent event) {
        if (!event.getEntity().level().isClientSide()) return;

        ImpactEffectManager impactManager = JujutsuSorcery.getInstance().getImpactEffectManager();
        if (impactManager != null) {
            impactManager.onEntityDeath(event.getEntity(), event.getSource());
        }
    }

    public float getCurrentFps() {
        return fps;
    }
}
