package com.anastas1s12.jjs.system.shader.render.impact;

import net.minecraft.client.gui.GuiGraphics;

/**
 * Manga/anime style impact frame effect.
 */
public class MangaFrameEffect extends ImpactEffect {
    private final String style;

    public MangaFrameEffect(String style, int durationMs) {
        super(durationMs);
        this.style = style;
    }

    @Override
    public void render(GuiGraphics graphics, float partialTicks) {
        float progress = getEasedProgress();
        float intensity = 1.0f - progress;

        int width = graphics.guiWidth();
        int height = graphics.guiHeight();

        if ("speed_lines".equals(style)) {
            // Draw radiating speed lines
            int centerX = width / 2;
            int centerY = height / 2;
            int lineCount = 24;

            for (int i = 0; i < lineCount; i++) {
                float angle = (float)(i * Math.PI * 2 / lineCount + progress * Math.PI);
                int x1 = centerX + (int)(Math.cos(angle) * 50);
                int y1 = centerY + (int)(Math.sin(angle) * 50);
                int x2 = centerX + (int)(Math.cos(angle) * (300 + intensity * 200));
                int y2 = centerY + (int)(Math.sin(angle) * (300 + intensity * 200));

                int alpha = (int)(intensity * 200);
                // Draw line using fill
                graphics.fill(x1 - 1, y1 - 1, x2 + 1, y2 + 1, (alpha << 24) | 0xFFFFFF);
            }
        } else if ("impact_frame".equals(style)) {
            // Black and white high contrast frame
            int alpha = (int)(intensity * 255);
            graphics.fill(0, 0, width, height, (alpha << 24) | 0xFFFFFF);

            // Black border
            int border = (int)(intensity * 20);
            graphics.fill(0, 0, width, border, 0xFF000000);
            graphics.fill(0, height - border, width, height, 0xFF000000);
            graphics.fill(0, 0, border, height, 0xFF000000);
            graphics.fill(width - border, 0, width, height, 0xFF000000);
        }
    }
}
