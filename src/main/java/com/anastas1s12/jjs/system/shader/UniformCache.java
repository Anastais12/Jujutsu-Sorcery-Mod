package com.anastas1s12.jjs.system.shader;

import net.minecraft.client.Minecraft;
import org.lwjgl.opengl.GL11;

import java.util.HashMap;
import java.util.Map;

/**
 * UniformCache - Manages runtime uniform values for all active shaders.
 *
 * Updates global uniforms every frame:
 * - Time (seconds since shader load)
 * - Resolution (screen width/height)
 * - Mouse position
 * - Frame count
 * - Game tick delta
 *
 * Also provides an API for external mods to set custom uniforms.
 */
public class UniformCache {

    private static final Minecraft MC = Minecraft.getInstance();

    // Global uniform state
    private float shaderTime = 0f;
    private int frameCount = 0;
    private long startTime = System.currentTimeMillis();

    // Custom uniforms set via API or GUI
    private final Map<String, Map<String, Object>> customUniforms = new HashMap<>();

    public void updateGlobalUniforms(CustomShaderInstance shader, float partialTicks) {
        if (!shader.isActive()) return;

        // Time in seconds
        shaderTime = (System.currentTimeMillis() - startTime) / 1000.0f;
        if (shader.hasUniform("Time")) {
            shader.setUniformFloat("Time", shaderTime);
        }
        if (shader.hasUniform("time")) {
            shader.setUniformFloat("time", shaderTime);
        }

        // Resolution
        int width = MC.getWindow().getWidth();
        int height = MC.getWindow().getHeight();
        if (shader.hasUniform("InSize")) {
            shader.setUniformVec2("InSize", (float) width, (float) height);
        }
        if (shader.hasUniform("Resolution")) {
            shader.setUniformVec2("Resolution", (float) width, (float) height);
        }
        if (shader.hasUniform("iResolution")) {
            shader.setUniformVec3("iResolution", (float) width, (float) height, 1.0f);
        }

        // Mouse position (normalized 0-1)
        double mouseX = MC.mouseHandler.xpos() / width;
        double mouseY = 1.0 - (MC.mouseHandler.ypos() / height); // Flip Y
        if (shader.hasUniform("Mouse")) {
            shader.setUniformVec2("Mouse", (float) mouseX, (float) mouseY);
        }
        if (shader.hasUniform("iMouse")) {
            shader.setUniformVec4("iMouse", (float) mouseX, (float) mouseY, 0f, 0f);
        }

        // Frame count
        if (shader.hasUniform("Frame")) {
            shader.setUniformInt("Frame", frameCount);
        }

        // Game time
        if (MC.level != null) {
            float dayTime = MC.level.getDayTime() / 24000.0f;
            if (shader.hasUniform("DayTime")) {
                shader.setUniformFloat("DayTime", dayTime);
            }
        }

        // Player health (for impact effects)
        if (MC.player != null) {
            float health = MC.player.getHealth() / MC.player.getMaxHealth();
            if (shader.hasUniform("PlayerHealth")) {
                shader.setUniformFloat("PlayerHealth", health);
            }
        }

        // Apply custom uniforms
        Map<String, Object> shaderCustom = customUniforms.get(shader.getName());
        if (shaderCustom != null) {
            for (Map.Entry<String, Object> entry : shaderCustom.entrySet()) {
                applyCustomUniform(shader, entry.getKey(), entry.getValue());
            }
        }

        frameCount++;
    }

    private void applyCustomUniform(CustomShaderInstance shader, String name, Object value) {
        if (!shader.hasUniform(name)) return;

        if (value instanceof Float f) {
            shader.setUniformFloat(name, f);
        } else if (value instanceof Integer i) {
            shader.setUniformInt(name, i);
        } else if (value instanceof Boolean b) {
            shader.setUniformBool(name, b);
        } else if (value instanceof float[] arr) {
            if (arr.length == 2) shader.setUniformVec2(name, arr[0], arr[1]);
            else if (arr.length == 3) shader.setUniformVec3(name, arr[0], arr[1], arr[2]);
            else if (arr.length == 4) shader.setUniformVec4(name, arr[0], arr[1], arr[2], arr[3]);
        }
    }

    /**
     * Set a custom uniform value for a specific shader.
     */
    public void setCustomUniform(String shaderName, String uniformName, Object value) {
        customUniforms.computeIfAbsent(shaderName, k -> new HashMap<>()).put(uniformName, value);
    }

    /**
     * Get a custom uniform value.
     */
    public Object getCustomUniform(String shaderName, String uniformName) {
        Map<String, Object> shaderUniforms = customUniforms.get(shaderName);
        return shaderUniforms != null ? shaderUniforms.get(uniformName) : null;
    }

    /**
     * Remove a custom uniform.
     */
    public void removeCustomUniform(String shaderName, String uniformName) {
        Map<String, Object> shaderUniforms = customUniforms.get(shaderName);
        if (shaderUniforms != null) {
            shaderUniforms.remove(uniformName);
        }
    }

    /**
     * Reset all custom uniforms for a shader.
     */
    public void clearCustomUniforms(String shaderName) {
        customUniforms.remove(shaderName);
    }

    /**
     * Reset the global timer (call on shader reload).
     */
    public void resetTimer() {
        startTime = System.currentTimeMillis();
        shaderTime = 0f;
        frameCount = 0;
    }

    public float getShaderTime() {
        return shaderTime;
    }

    public int getFrameCount() {
        return frameCount;
    }
}
