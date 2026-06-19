package com.anastas1s12.jjs.system.shader.gui.components;

import com.anastas1s12.jjs.system.shader.data.project.ShaderProject;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;

import java.util.function.BiConsumer;

/**
 * Right panel of the Shader Workbench GUI.
 * Edits shader properties: name, render pass, blend mode, uniforms.
 */
public class PropertiesPanel extends AbstractWidget {

    private ShaderProject currentProject;
    private final BiConsumer<String, Object> onPropertyChanged;

    public PropertiesPanel(int x, int y, int width, int height,
                           BiConsumer<String, Object> onPropertyChanged) {
        super(x, y, width, height, Component.literal("Properties"));
        this.onPropertyChanged = onPropertyChanged;
    }

    public void setProject(ShaderProject project) {
        this.currentProject = project;
    }

    @Override
    public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        graphics.fill(getX(), getY(), getX() + width, getY() + height, 0xFF1E1E1E);

        graphics.fill(getX(), getY(), getX() + width, getY() + 16, 0xFF2D2D30);
        graphics.drawCenteredString(Minecraft.getInstance().font,
                "§lProperties", getX() + width / 2, getY() + 4, 0xFFFFFF);

        if (currentProject == null) {
            graphics.drawString(Minecraft.getInstance().font,
                    "No project selected", getX() + 10, getY() + 40, 0xFF888888);
            return;
        }

        int y = getY() + 24;
        int labelColor = 0xFFAAAAAA;
        int valueColor = 0xFFFFFFFF;

        graphics.drawString(Minecraft.getInstance().font, "Name:", getX() + 8, y, labelColor);
        y += 12;
        graphics.drawString(Minecraft.getInstance().font, currentProject.getName(), getX() + 12, y, valueColor);
        y += 24;

        graphics.drawString(Minecraft.getInstance().font, "Render Pass:", getX() + 8, y, labelColor);
        y += 12;
        Object pass = currentProject.getProperties().getOrDefault("renderPass", "post");
        graphics.drawString(Minecraft.getInstance().font, pass.toString(), getX() + 12, y, valueColor);
        y += 24;

        graphics.drawString(Minecraft.getInstance().font, "Blend Mode:", getX() + 8, y, labelColor);
        y += 12;
        Object blend = currentProject.getProperties().getOrDefault("blendMode", "alpha");
        graphics.drawString(Minecraft.getInstance().font, blend.toString(), getX() + 12, y, valueColor);
        y += 24;

        graphics.drawString(Minecraft.getInstance().font, "Priority:", getX() + 8, y, labelColor);
        y += 12;
        Object priority = currentProject.getProperties().getOrDefault("priority", 100);
        graphics.drawString(Minecraft.getInstance().font, priority.toString(), getX() + 12, y, valueColor);
        y += 24;

        graphics.drawString(Minecraft.getInstance().font, "§lUniforms:", getX() + 8, y, 0xFF569CD6);
        y += 14;

        for (var entry : currentProject.getProperties().entrySet()) {
            if (entry.getKey().equals("renderPass") || entry.getKey().equals("blendMode")
                    || entry.getKey().equals("priority")) continue;

            graphics.drawString(Minecraft.getInstance().font,
                    entry.getKey() + ":", getX() + 12, y, labelColor);
            y += 10;
            String valStr = entry.getValue() != null ? entry.getValue().toString() : "null";
            if (valStr.length() > 20) valStr = valStr.substring(0, 20) + "...";
            graphics.drawString(Minecraft.getInstance().font,
                    "  " + valStr, getX() + 16, y, valueColor);
            y += 14;

            if (y > getY() + height - 20) break;
        }
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput output) {
        this.defaultButtonNarrationText(output);
    }
}
