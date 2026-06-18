package com.anastas1s12.jjs.system.shaders;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.MultiLineEditBox;
import net.minecraft.network.chat.Component;

public class ShaderIDEScreen extends Screen {
    private MultiLineEditBox codeEditor;
    private EditBox fileNameBox;

    public ShaderIDEScreen() {
        super(Component.literal("Live Shader IDE"));
    }

    @Override
    protected void init() {
        super.init();
        int centerX = this.width / 2;

        // Top controls
        this.fileNameBox = new EditBox(this.font, 10, 10, 150, 20, Component.literal("Filename"));
        this.fileNameBox.setValue("my_custom_shader.fsh");
        this.addRenderableWidget(this.fileNameBox);

        this.addRenderableWidget(Button.builder(Component.literal("Load"), b -> loadFromFile())
                .bounds(170, 10, 60, 20).build());
        this.addRenderableWidget(Button.builder(Component.literal("Save"), b -> saveToFile())
                .bounds(235, 10, 60, 20).build());
        this.addRenderableWidget(Button.builder(Component.literal("Apply to Screen"), b -> applyShader())
                .bounds(300, 10, 120, 20).build());
        this.addRenderableWidget(Button.builder(Component.literal("Close"), b -> this.minecraft.setScreen(null))
                .bounds(this.width - 70, 10, 60, 20).build());

        // The main text editor for GLSL code
        this.codeEditor = new MultiLineEditBox(this.font, 10, 40, this.width - 20, this.height - 50, Component.literal("Code Editor"), Component.literal("GLSL Code"));

        // Put some default code in the box
        this.codeEditor.setValue(
                "#version 150\n\n" +
                        "uniform sampler2D DiffuseSampler;\n" +
                        "in vec2 texCoord;\n" +
                        "out vec4 fragColor;\n\n" +
                        "void main() {\n" +
                        "    vec4 color = texture(DiffuseSampler, texCoord);\n" +
                        "    fragColor = vec4(1.0 - color.r, 1.0 - color.g, 1.0 - color.b, color.a); // Invert colors\n" +
                        "}"
        );
        this.addRenderableWidget(this.codeEditor);
    }

    @Override
    public void render(net.minecraft.client.gui.GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(graphics);
        super.render(graphics, mouseX, mouseY, partialTick);
    }

    // Stub methods for the logic
    private void loadFromFile() { ShaderIO.loadCode(this.fileNameBox.getValue(), this.codeEditor); }
    private void saveToFile() { ShaderIO.saveCode(this.fileNameBox.getValue(), this.codeEditor.getValue()); }
    private void applyShader() {
        saveToFile();
        ShaderIO.compileAndApply(this.fileNameBox.getValue());
    }
}