package com.anastas1s12.jjs.system.shader.render.impact;

import com.anastas1s12.jjs.JujutsuSorcery;
import com.anastas1s12.jjs.config.SWBConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.damagesource.DamageSource;

import java.util.*;

/**
 * Manages impact frame effects for combat/action sequences.
 *
 * Example sequence: Critical hit → Freeze 50ms → White flash → RGB split → Normal
 */
public class ImpactEffectManager {
    private final Minecraft mc = Minecraft.getInstance();
    private final List<ImpactEffect> activeEffects = new ArrayList<>();
    private final Map<String, ImpactEffectFactory> effectRegistry = new HashMap<>();
    private final Queue<ImpactEffect> pendingEffects = new LinkedList<>();

    private boolean debugMode = false;
    private long lastTriggerTime = 0;

    public ImpactEffectManager() {
        registerDefaultEffects();
    }

    private void registerDefaultEffects() {
        // Screen freeze
        register("freeze", (params) -> new ScreenFreezeEffect(
                (int) params.getOrDefault("duration", 50)));

        // Flash effects
        register("white_flash", (params) -> new FlashEffect(
                1.0f, 1.0f, 1.0f, (int) params.getOrDefault("duration", 100)));
        register("black_flash", (params) -> new FlashEffect(
                0.0f, 0.0f, 0.0f, (int) params.getOrDefault("duration", 100)));

        // Color effects
        register("color_invert", (params) -> new ColorInvertEffect(
                (int) params.getOrDefault("duration", 200)));
        register("rgb_split", (params) -> new RGBSplitEffect(
                (float) params.getOrDefault("intensity", 0.02f),
                (int) params.getOrDefault("duration", 300)));

        // Blur effects
        register("radial_blur", (params) -> new RadialBlurEffect(
                (float) params.getOrDefault("strength", 0.5f),
                (int) params.getOrDefault("duration", 400)));
        register("motion_blur", (params) -> new MotionBlurEffect(
                (float) params.getOrDefault("strength", 0.8f),
                (int) params.getOrDefault("duration", 300)));

        // Zoom effects
        register("zoom_burst", (params) -> new ZoomBurstEffect(
                (float) params.getOrDefault("zoom", 1.5f),
                (int) params.getOrDefault("duration", 250)));

        // Screen shake
        register("screen_shake", (params) -> new ScreenShakeEffect(
                (float) params.getOrDefault("intensity", 5.0f),
                (int) params.getOrDefault("duration", 500)));

        // Overlays
        register("hit_spark", (params) -> new HitSparkOverlay(
                (int) params.getOrDefault("count", 8),
                (int) params.getOrDefault("duration", 300)));

        // Manga/anime style
        register("manga_frame", (params) -> new MangaFrameEffect(
                (String) params.getOrDefault("style", "speed_lines"),
                (int) params.getOrDefault("duration", 400)));

        // Edge effects
        register("edge_glow", (params) -> new EdgeGlowEffect(
                (int) params.getOrDefault("color", 0xFF0000),
                (int) params.getOrDefault("duration", 350)));

        // Preset combinations
        register("critical_hit", (params) -> new SequenceEffect(Arrays.asList(
                new ScreenFreezeEffect(50),
                new FlashEffect(1.0f, 1.0f, 1.0f, 80),
                new RGBSplitEffect(0.03f, 200),
                new ScreenShakeEffect(3.0f, 300)
        )));

        register("boss_intro", (params) -> new SequenceEffect(Arrays.asList(
                new ScreenFreezeEffect(100),
                new FlashEffect(0.0f, 0.0f, 0.0f, 200),
                new ZoomBurstEffect(2.0f, 500),
                new EdgeGlowEffect(0xFF0000, 1000)
        )));
    }

    public void register(String name, ImpactEffectFactory factory) {
        effectRegistry.put(name.toLowerCase(), factory);
    }

    public boolean triggerEffect(String name) {
        return triggerEffect(name, new HashMap<>());
    }

    public boolean triggerEffect(String name, Map<String, Object> params) {
        if (!SWBConfig.IMPACT_FRAMES_ENABLED.get()) return false;

        ImpactEffectFactory factory = effectRegistry.get(name.toLowerCase());
        if (factory == null) return false;

        if (activeEffects.size() >= SWBConfig.MAX_CONCURRENT_EFFECTS.get()) {
            JujutsuSorcery.LOGGER.warn("Max concurrent effects reached, dropping: {}", name);
            return false;
        }

        ImpactEffect effect = factory.create(params);
        effect.start();
        activeEffects.add(effect);
        lastTriggerTime = System.currentTimeMillis();

        if (debugMode) {
            JujutsuSorcery.LOGGER.info("Triggered impact effect: {}", name);
        }
        return true;
    }

    public void onPlayerAttack(Player player, Entity target) {
        boolean isCritical = player.fallDistance > 0.0f && !player.onGround()
                && !player.onClimbable() && !player.isInWater() && !player.hasEffect(net.minecraft.world.effect.MobEffects.BLINDNESS);

        if (isCritical) {
            triggerEffect("critical_hit");
        } else {
            triggerEffect("hit_spark", Map.of("count", 4));
        }
    }

    public void onEntityDeath(LivingEntity entity, DamageSource source) {
        if (source.getEntity() instanceof Player) {
            triggerEffect("boss_intro"); // Scale based on entity type
        }
    }

    public void tick() {
        // Update all active effects
        Iterator<ImpactEffect> it = activeEffects.iterator();
        while (it.hasNext()) {
            ImpactEffect effect = it.next();
            effect.tick();
            if (effect.isFinished()) {
                effect.cleanup();
                it.remove();
            }
        }
    }

    public void render(GuiGraphics graphics, float partialTicks) {
        for (ImpactEffect effect : activeEffects) {
            if (effect.isActive()) {
                effect.render(graphics, partialTicks);
            }
        }
    }

    public void testAllEffects() {
        String[] effects = getAvailableEffects().toArray(new String[0]);
        int delay = 0;
        for (String effect : effects) {
            final String e = effect;
            final int currentDelay = delay;

            new Thread(() -> {
                try {
                    Thread.sleep(currentDelay);
                    mc.execute(() -> triggerEffect(e));
                } catch (InterruptedException ignored) {}
            }).start();

            delay += 500;
        }
    }


    public void stopAllEffects() {
        for (ImpactEffect effect : activeEffects) {
            effect.cleanup();
        }
        activeEffects.clear();
    }

    public List<String> getAvailableEffects() {
        return new ArrayList<>(effectRegistry.keySet());
    }

    public void toggleDebug() {
        debugMode = !debugMode;
    }

    public boolean isDebugMode() {
        return debugMode;
    }

    public List<ImpactEffect> getActiveEffects() {
        return new ArrayList<>(activeEffects);
    }

    @FunctionalInterface
    public interface ImpactEffectFactory {
        ImpactEffect create(Map<String, Object> params);
    }
}