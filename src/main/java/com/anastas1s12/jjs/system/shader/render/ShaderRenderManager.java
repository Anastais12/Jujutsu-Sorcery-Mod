package com.anastas1s12.jjs.system.shader.render;

import com.anastas1s12.jjs.JujutsuSorcery;
import com.anastas1s12.jjs.system.shader.CustomShaderInstance;
import com.anastas1s12.jjs.system.shader.ShaderCompilationResult;
import com.anastas1s12.jjs.system.shader.UniformCache;
import com.mojang.blaze3d.shaders.Uniform;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.PostChain;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import org.lwjgl.opengl.GL20;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Central shader management system.
 * Handles compilation, caching, hot-reloading, and uniform management.
 */
public class ShaderRenderManager {
    private final Minecraft mc = Minecraft.getInstance();
    private final Map<String, CustomShaderInstance> loadedShaders = new ConcurrentHashMap<>();
    private final Map<String, ShaderCompilationResult> compileLog = new ConcurrentHashMap<>();
    private final UniformCache uniformCache = new UniformCache();

    private boolean debugMode = false;
    private long totalFrameTime = 0;
    private int frameSamples = 0;

    public void reloadAllShaders() {
        loadedShaders.clear();
        compileLog.clear();
        JujutsuSorcery.LOGGER.info("All shaders cleared for reload.");
    }

    /**
     * Compile a shader from source files with async support.
     */
    public boolean compileShader(String name) {
        try {
            CustomShaderInstance shader = new CustomShaderInstance(name);
            ShaderCompilationResult result = shader.compile();
            compileLog.put(name, result);

            if (result.isSuccess()) {
                loadedShaders.put(name, shader);
                JujutsuSorcery.LOGGER.info("Shader '{}' compiled successfully.", name);
                return true;
            } else {
                JujutsuSorcery.LOGGER.error("Shader '{}' compilation failed: {}",
                        name, result.getErrorMessage());
                return false;
            }
        } catch (Exception e) {
            compileLog.put(name, new ShaderCompilationResult(false, e.getMessage(), -1));
            return false;
        }
    }

    /**
     * Async compilation for non-blocking editor experience.
     */
    public CompletableFuture<Boolean> compileShaderAsync(String name) {
        return CompletableFuture.supplyAsync(() -> compileShader(name));
    }

    public void updateUniforms(float partialTicks) {
        for (CustomShaderInstance shader : loadedShaders.values()) {
            if (shader.isActive()) {
                uniformCache.updateGlobalUniforms(shader, partialTicks);
            }
        }
    }

    public CustomShaderInstance getShader(String name) {
        return loadedShaders.get(name);
    }

    public Set<String> getLoadedShaderNames() {
        return new HashSet<>(loadedShaders.keySet());
    }

    public ShaderCompilationResult getCompileResult(String name) {
        return compileLog.get(name);
    }

    public void toggleDebugMode() {
        debugMode = !debugMode;
    }

    public boolean isDebugMode() {
        return debugMode;
    }

    public void runBenchmark() {
        // Benchmark all active shaders
        totalFrameTime = 0;
        frameSamples = 0;
        JujutsuSorcery.LOGGER.info("Starting shader benchmark...");

        for (Map.Entry<String, CustomShaderInstance> entry : loadedShaders.entrySet()) {
            if (entry.getValue().isActive()) {
                long start = System.nanoTime();
                entry.getValue().benchmark();
                long elapsed = System.nanoTime() - start;
                JujutsuSorcery.LOGGER.info("Shader '{}' benchmark: {} ms",
                        entry.getKey(), elapsed / 1_000_000.0);
            }
        }
    }

    public void resetAllShaders() {
        loadedShaders.values().forEach(CustomShaderInstance::reset);
        JujutsuSorcery.LOGGER.info("All shaders reset.");
    }

    public void cleanup() {
        loadedShaders.values().forEach(CustomShaderInstance::dispose);
        loadedShaders.clear();
    }
}