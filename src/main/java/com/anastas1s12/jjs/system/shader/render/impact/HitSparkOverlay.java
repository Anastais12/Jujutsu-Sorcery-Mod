package com.anastas1s12.jjs.system.shader.render.impact;

import net.minecraft.client.gui.GuiGraphics;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Hit spark particle overlay effect.
 */
public class HitSparkOverlay extends ImpactEffect {
    private final int sparkCount;
    private final List<Spark> sparks = new ArrayList<>();
    private final Random random = new Random();

    private static class Spark {
        float x, y;
        float vx, vy;
        float life;
        int color;
    }

    public HitSparkOverlay(int count, int durationMs) {
        super(durationMs);
        this.sparkCount = count;
    }

    @Override
    public void start() {
        super.start();
        int centerX = 960; // Approximate screen center (1920/2)
        int centerY = 540; // Approximate screen center (1080/2)

        for (int i = 0; i < sparkCount; i++) {
            Spark spark = new Spark();
            spark.x = centerX;
            spark.y = centerY;
            float angle = random.nextFloat() * (float)(Math.PI * 2);
            float speed = 2 + random.nextFloat() * 8;
            spark.vx = (float)Math.cos(angle) * speed;
            spark.vy = (float)Math.sin(angle) * speed;
            spark.life = 1.0f;
            spark.color = random.nextBoolean() ? 0xFFFFFF00 : 0xFFFFAA00; // Yellow/Gold
            sparks.add(spark);
        }
    }

    @Override
    public void tick() {
        super.tick();
        float dt = 0.016f; // Approximate delta time
        for (Spark spark : sparks) {
            spark.x += spark.vx;
            spark.y += spark.vy;
            spark.vy += 0.2f; // Gravity
            spark.life -= dt * 2;
        }
    }

    @Override
    public void render(GuiGraphics graphics, float partialTicks) {
        for (Spark spark : sparks) {
            if (spark.life <= 0) continue;
            int alpha = (int)(spark.life * 255);
            int color = (alpha << 24) | (spark.color & 0x00FFFFFF);
            int size = (int)(spark.life * 4);
            graphics.fill((int)spark.x - size, (int)spark.y - size,
                (int)spark.x + size, (int)spark.y + size, color);
        }
    }

    @Override
    public void cleanup() {
        sparks.clear();
        super.cleanup();
    }
}
