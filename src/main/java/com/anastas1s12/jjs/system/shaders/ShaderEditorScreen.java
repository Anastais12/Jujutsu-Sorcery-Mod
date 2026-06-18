package com.anastas1s12.jjs.system.shaders;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import net.minecraft.client.Minecraft;

public class ShaderEditorScreen extends Screen {
    private EditBox presetNameBox;

    public ShaderEditorScreen() {
        super(Component.literal("Impact Shader Editor"));
    }

    @Override
    protected void init() {
        super.init();
        int centerX = this.width / 2;

        // Top Row Buttons
        this.addRenderableWidget(Button.builder(Component.literal("Reset"), b -> resetDefaults())
                .bounds(centerX - 150, 10, 60, 20).build());
        this.addRenderableWidget(Button.builder(Component.literal("Gameplay: OFF"), b -> toggleGameplay())
                .bounds(centerX - 80, 10, 160, 20).build());
        this.addRenderableWidget(Button.builder(Component.literal("Done"), b -> this.minecraft.setScreen(null))
                .bounds(centerX + 90, 10, 60, 20).build());

        // Preset Saving Row
        this.presetNameBox = new EditBox(this.font, centerX - 150, 35, 230, 20, Component.literal("Preset Name"));
        this.presetNameBox.setValue("preset name");
        this.addRenderableWidget(this.presetNameBox);

        this.addRenderableWidget(Button.builder(Component.literal("Save"), b -> savePreset())
                .bounds(centerX + 90, 35, 60, 20).build());

        // --- SLIDERS ---
        int leftCol = centerX - 150;
        int rightCol = centerX + 5;

        // COLOR Category (Y = 100)
        this.addRenderableWidget(new ShaderSlider(leftCol, 100, 140, 20, Component.literal("Invert"), 0.0, 1.0, 0.68, this::updateShader));
        this.addRenderableWidget(new ShaderSlider(leftCol, 125, 140, 20, Component.literal("Desaturate"), 0.0, 1.0, 1.00, this::updateShader));
        this.addRenderableWidget(new ShaderSlider(leftCol, 150, 140, 20, Component.literal("Saturation"), 0.0, 3.0, 1.83, this::updateShader));

        // TONE Category (Y = 100, Right Column for space, or put below if making a scrolling list)
        this.addRenderableWidget(new ShaderSlider(rightCol, 100, 140, 20, Component.literal("Contrast"), 0.0, 3.0, 1.88, this::updateShader));
        this.addRenderableWidget(new ShaderSlider(rightCol, 125, 140, 20, Component.literal("Brightness"), 0.0, 3.0, 1.60, this::updateShader));
        this.addRenderableWidget(new ShaderSlider(rightCol, 150, 140, 20, Component.literal("Bloom"), 0.0, 5.0, 1.64, this::updateShader));
    }

    @Override
    public void render(net.minecraft.client.gui.GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics); // Renders the dark background

        // Draw Category Headers
        guiGraphics.drawString(this.font, "COLOR", this.width / 2 - 150, 85, 0x00FFCC, false);
        guiGraphics.drawString(this.font, "TONE", this.width / 2 + 5, 85, 0x00FFCC, false);

        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    // --- LOGIC METHODS ---
    private void updateShader(String uniformName, double value) {
        // This is where you pass the value to your active post-chain
        ShaderManager.setUniform(uniformName, (float) value);
    }

    private void resetDefaults() { /* Reset logic */ }
    private void toggleGameplay() { /* Toggle logic */ }
    private void savePreset() {
        String name = presetNameBox.getValue();
        // Save logic to JSON here
    }
}