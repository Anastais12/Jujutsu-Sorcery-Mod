package com.anastas1s12.jjs.system.shader.gui.components;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Bottom console panel showing compilation logs, errors, and warnings.
 */
public class ConsolePanel extends AbstractWidget {

    public enum LogLevel {
        INFO(0xFFAAAAAA),
        SUCCESS(0xFF4EC9B0),
        WARNING(0xFFDCDCAA),
        ERROR(0xFFF44747);

        public final int color;
        LogLevel(int color) { this.color = color; }
    }

    private static class LogEntry {
        final String message;
        final LogLevel level;
        final long timestamp;

        LogEntry(String message, LogLevel level) {
            this.message = message;
            this.level = level;
            this.timestamp = System.currentTimeMillis();
        }
    }

    private final List<LogEntry> entries = new ArrayList<>();
    private int scrollOffset = 0;
    private static final int LINE_HEIGHT = 11;
    private static final int MAX_ENTRIES = 200;

    public ConsolePanel(int x, int y, int width, int height) {
        super(x, y, width, height, Component.literal("Console"));
    }

    public void log(String message, LogLevel level) {
        entries.add(new LogEntry(message, level));
        if (entries.size() > MAX_ENTRIES) {
            entries.remove(0);
        }
        scrollOffset = Math.max(0, entries.size() - visibleLines());
    }

    public void clear() {
        entries.clear();
        scrollOffset = 0;
    }

    @Override
    public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        graphics.fill(getX(), getY(), getX() + width, getY() + height, 0xFF0C0C0C);

        graphics.fill(getX(), getY(), getX() + width, getY() + 14, 0xFF2D2D30);
        graphics.drawString(Minecraft.getInstance().font,
                "§lConsole", getX() + 6, getY() + 3, 0xFFFFFF);

        int contentY = getY() + 16;
        int visible = visibleLines();

        for (int i = 0; i < visible; i++) {
            int idx = scrollOffset + i;
            if (idx >= entries.size()) break;

            LogEntry entry = entries.get(idx);
            int y = contentY + i * LINE_HEIGHT;

            String prefix = switch (entry.level) {
                case INFO -> "[INFO] ";
                case SUCCESS -> "[OK] ";
                case WARNING -> "[WARN] ";
                case ERROR -> "[ERR] ";
            };

            String fullMsg = prefix + entry.message;
            if (fullMsg.length() > 120) {
                fullMsg = fullMsg.substring(0, 120);
            }

            graphics.drawString(Minecraft.getInstance().font, fullMsg, getX() + 6, y, entry.level.color);
        }
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        int visible = visibleLines();
        int maxScroll = Math.max(0, entries.size() - visible);

        if (delta > 0) {
            scrollOffset = Math.max(0, scrollOffset - 3);
        } else {
            scrollOffset = Math.min(maxScroll, scrollOffset + 3);
        }
        return true;
    }

    private int visibleLines() {
        return (height - 20) / LINE_HEIGHT;
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput output) {
        this.defaultButtonNarrationText(output);
    }
}
