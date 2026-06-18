package com.anastas1s12.jjs.system.shaders;

import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.network.chat.Component;

public class ShaderSlider extends AbstractSliderButton {
    private final String label;
    private final double minValue;
    private final double maxValue;
    private final ShaderUniformCallback callback;
    private double actualValue;

    public ShaderSlider(int x, int y, int width, int height, Component title, double min, double max, double initial, ShaderUniformCallback callback) {
        super(x, y, width, height, Component.empty(), 0.0);
        this.label = title.getString();
        this.minValue = min;
        this.maxValue = max;
        this.callback = callback;

        // Convert initial value to 0.0 - 1.0 range for the slider knob
        this.value = (initial - min) / (max - min);
        this.updateMessage();
    }

    @Override
    protected void updateMessage() {
        this.actualValue = minValue + (value * (maxValue - minValue));
        // Format to 2 decimal places like the screenshot
        this.setMessage(Component.literal(String.format("%s: %.2f", label, actualValue)));
    }

    @Override
    protected void applyValue() {
        // Send the updated value to your shader manager
        this.callback.onValueChanged(this.label, this.actualValue);
    }

    // Functional interface to pass values back to the screen/shader manager
    public interface ShaderUniformCallback {
        void onValueChanged(String uniformName, double newValue);
    }
}