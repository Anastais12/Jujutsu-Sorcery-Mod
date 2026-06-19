package com.anastas1s12.jjs.system.shader.render.impact;

import net.minecraft.client.gui.GuiGraphics;

import java.util.List;

/**
 * Chains multiple impact effects in sequence.
 * Example: Freeze → Flash → RGB Split → Normal
 */
public class SequenceEffect extends ImpactEffect {
    private final List<ImpactEffect> effects;
    private int currentIndex = 0;

    public SequenceEffect(List<ImpactEffect> effects) {
        super(calculateTotalDuration(effects));
        this.effects = effects;
    }

    private static int calculateTotalDuration(List<ImpactEffect> effects) {
        int total = 0;
        for (ImpactEffect e : effects) {
            total += e.durationMs;
        }
        return total;
    }

    @Override
    public void start() {
        super.start();
        currentIndex = 0;
        if (!effects.isEmpty()) {
            effects.get(0).start();
        }
    }

    @Override
    public void tick() {
        super.tick();

        long elapsed = System.currentTimeMillis() - startTime;
        int accumulated = 0;

        for (int i = 0; i < effects.size(); i++) {
            ImpactEffect effect = effects.get(i);
            if (elapsed >= accumulated && elapsed < accumulated + effect.durationMs) {
                if (currentIndex != i) {
                    if (currentIndex < effects.size()) {
                        effects.get(currentIndex).cleanup();
                    }
                    currentIndex = i;
                    effect.start();
                    effect.startTime = startTime + accumulated;
                }
                effect.tick();
                break;
            }
            accumulated += effect.durationMs;
        }
    }

    @Override
    public void render(GuiGraphics graphics, float partialTicks) {
        if (currentIndex < effects.size()) {
            effects.get(currentIndex).render(graphics, partialTicks);
        }
    }

    @Override
    public void cleanup() {
        for (ImpactEffect effect : effects) {
            effect.cleanup();
        }
        super.cleanup();
    }
}
