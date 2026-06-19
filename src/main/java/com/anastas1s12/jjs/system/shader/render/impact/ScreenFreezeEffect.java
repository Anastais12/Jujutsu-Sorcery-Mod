package com.anastas1s12.jjs.system.shader.render.impact;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;

/**
 * Screen freeze - pauses rendering updates for a brief moment.
 */
public class ScreenFreezeEffect extends ImpactEffect {
    public ScreenFreezeEffect(int durationMs) {
        super(durationMs);
    }

    @Override
    public void render(GuiGraphics graphics, float partialTicks) {
        // Freeze is handled by the render system - this effect
        // simply marks that the screen should not update during its duration
        // The actual freeze logic would be in the render event handler
    }

    public boolean shouldFreeze() {
        return active && !finished;
    }
}
