package com.anastas1s12.jjs.system.shader.render.impact;

import net.minecraft.client.gui.GuiGraphics;

/**
 * Base class for all impact frame effects.
 */
public abstract class ImpactEffect {
    protected long startTime;
    protected int durationMs;
    protected boolean active = true;
    protected boolean finished = false;

    public ImpactEffect(int durationMs) {
        this.durationMs = durationMs;
    }

    public void start() {
        this.startTime = System.currentTimeMillis();
        this.active = true;
        this.finished = false;
    }

    public void tick() {
        if (finished) return;

        long elapsed = System.currentTimeMillis() - startTime;
        if (elapsed >= durationMs) {
            finished = true;
            active = false;
        }
    }

    public abstract void render(GuiGraphics graphics, float partialTicks);

    public void cleanup() {
        active = false;
        finished = true;
    }

    public boolean isActive() {
        return active;
    }

    public boolean isFinished() {
        return finished;
    }

    protected float getProgress() {
        return Math.min(1.0f, (System.currentTimeMillis() - startTime) / (float) durationMs);
    }

    protected float getEasedProgress() {
        float t = getProgress();
        // Ease-out cubic
        return 1.0f - (1.0f - t) * (1.0f - t) * (1.0f - t);
    }
}