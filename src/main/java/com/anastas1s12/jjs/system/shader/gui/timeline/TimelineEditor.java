package com.anastas1s12.jjs.system.shader.gui.timeline;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Visual timeline editor for impact frame effects.
 * Supports keyframes, curves, duration control, and event markers.
 */
public class TimelineEditor extends AbstractWidget {

    private float durationMs = 1000f;
    private float currentTime = 0f;
    private boolean playing = false;
    private long lastUpdate = 0;

    private final List<Keyframe> keyframes = new ArrayList<>();

    public TimelineEditor(int x, int y, int width, int height) {
        super(x, y, width, height, Component.literal("Timeline"));
    }

    public void addKeyframe(float time, float value, String property) {
        keyframes.add(new Keyframe(time, value, property));
        keyframes.sort((a, b) -> Float.compare(a.time, b.time));
    }

    public void clearKeyframes() {
        keyframes.clear();
    }

    public void setDuration(float ms) {
        this.durationMs = ms;
    }

    public void play() {
        playing = true;
        lastUpdate = System.currentTimeMillis();
    }

    public void pause() {
        playing = false;
    }

    public void stop() {
        playing = false;
        currentTime = 0f;
    }

    public void tick() {
        if (!playing) return;
        long now = System.currentTimeMillis();
        float delta = (now - lastUpdate) / durationMs;
        currentTime += delta;
        if (currentTime > 1.0f) {
            currentTime = 1.0f;
            playing = false;
        }
        lastUpdate = now;
    }

    @Override
    public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        graphics.fill(getX(), getY(), getX() + width, getY() + height, 0xFF1A1A1A);

        graphics.fill(getX(), getY(), getX() + width, getY() + 14, 0xFF2D2D30);
        graphics.drawString(Minecraft.getInstance().font,
                "§lTimeline  " + String.format("%.0fms", durationMs) + "  " +
                        (playing ? "▶" : "⏸") + " " + String.format("%.1f%%", currentTime * 100),
                getX() + 6, getY() + 3, 0xFFFFFF);

        int trackY = getY() + 22;
        int trackHeight = height - 28;
        int trackPadding = 20;
        int trackWidth = width - trackPadding * 2;

        graphics.fill(getX() + trackPadding, trackY,
                getX() + width - trackPadding, trackY + trackHeight, 0xFF2D2D30);

        for (Keyframe kf : keyframes) {
            int kfX = getX() + trackPadding + (int) (kf.time * trackWidth);
            int kfY = trackY + trackHeight / 2;
            int size = 4;
            graphics.fill(kfX - size, kfY - size, kfX + size, kfY + size, 0xFF569CD6);
        }

        int playheadX = getX() + trackPadding + (int) (currentTime * trackWidth);
        graphics.fill(playheadX - 1, trackY, playheadX + 1, trackY + trackHeight, 0xFFFF0000);

        for (int i = 0; i <= 10; i++) {
            int mx = getX() + trackPadding + (i * trackWidth / 10);
            graphics.fill(mx, trackY + trackHeight - 3, mx + 1, trackY + trackHeight, 0xFF666666);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int trackPadding = 20;
        int trackWidth = width - trackPadding * 2;
        int trackY = getY() + 22;
        int trackHeight = height - 28;

        if (mouseX >= getX() + trackPadding && mouseX <= getX() + width - trackPadding
                && mouseY >= trackY && mouseY <= trackY + trackHeight) {
            currentTime = (float) ((mouseX - (getX() + trackPadding)) / trackWidth);
            currentTime = Math.max(0f, Math.min(1f, currentTime));
            return true;
        }
        return false;
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput output) {
        this.defaultButtonNarrationText(output);
    }

    public record Keyframe(float time, float value, String property) {}
}
