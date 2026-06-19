package com.anastas1s12.jjs.system.shader;

import com.anastas1s12.jjs.JujutsuSorcery;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.shaders.Uniform;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceProvider;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.system.MemoryStack;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.IntBuffer;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * CustomShaderInstance - A hot-reloadable shader wrapper for ShaderWorkbench.
 *
 * This class wraps Minecraft's ShaderInstance system to provide:
 * - Runtime compilation from source strings (not just resource files)
 * - Hot-reloading without restarting Minecraft
 * - Custom uniform management
 * - Proper OpenGL resource cleanup to prevent memory leaks
 *
 * Architecture:
 *   CustomShaderInstance wraps a compiled OpenGL shader program.
 *   It can be compiled from raw GLSL source strings stored in ShaderProject.
 *   Uniforms are cached and updated per-frame via UniformCache.
 *   When recompiled, old GL resources are explicitly deleted.
 */
public class CustomShaderInstance {

    private static final Minecraft MC = Minecraft.getInstance();

    // Shader metadata
    private final String name;
    private String vertexSource;
    private String fragmentSource;
    private boolean active = false;
    private boolean disposed = false;

    // OpenGL handles
    private int programId = 0;
    private int vertexShaderId = 0;
    private int fragmentShaderId = 0;

    // Uniform cache for this shader
    private final Map<String, UniformHandle> uniforms = new HashMap<>();

    // Compilation metadata
    private long compileTime = 0;
    private int uniformCount = 0;

    /**
     * Lightweight wrapper for uniform locations and cached values.
     */
    public static class UniformHandle {
        public final int location;
        public final int type; // GL_FLOAT, GL_INT, etc.
        private Object cachedValue = null;

        public UniformHandle(int location, int type) {
            this.location = location;
            this.type = type;
        }

        public void setFloat(float v) {
            if (cachedValue instanceof Float && ((Float) cachedValue) == v) return;
            cachedValue = v;
            GL20.glUniform1f(location, v);
        }

        public void setInt(int v) {
            if (cachedValue instanceof Integer && ((Integer) cachedValue) == v) return;
            cachedValue = v;
            GL20.glUniform1i(location, v);
        }

        public void setBool(boolean v) {
            setInt(v ? 1 : 0);
        }

        public void setVec2(float x, float y) {
            GL20.glUniform2f(location, x, y);
        }

        public void setVec3(float x, float y, float z) {
            GL20.glUniform3f(location, x, y, z);
        }

        public void setVec4(float x, float y, float z, float w) {
            GL20.glUniform4f(location, x, y, z, w);
        }
    }

    public CustomShaderInstance(String name) {
        this.name = name;
    }

    public CustomShaderInstance(String name, String vertexSource, String fragmentSource) {
        this.name = name;
        this.vertexSource = vertexSource;
        this.fragmentSource = fragmentSource;
    }

    // ============================================================
    // COMPILATION
    // ============================================================

    /**
     * Compile the shader from current source strings.
     * Performs full cleanup of old resources before compiling new ones.
     *
     * @return ShaderCompilationResult with success status and error details
     */
    public ShaderCompilationResult compile() {
        long startTime = System.nanoTime();

        // Validate sources
        if (vertexSource == null || vertexSource.isEmpty()) {
            return new ShaderCompilationResult(false, "Vertex shader source is empty", -1);
        }
        if (fragmentSource == null || fragmentSource.isEmpty()) {
            return new ShaderCompilationResult(false, "Fragment shader source is empty", -1);
        }

        // Clean up old resources first (prevents memory leaks)
        disposeInternal();

        try {
            // Compile vertex shader
            vertexShaderId = compileShader(GL20.GL_VERTEX_SHADER, vertexSource, "vertex");
            if (vertexShaderId == 0) {
                return new ShaderCompilationResult(false, extractLog(GL20.GL_VERTEX_SHADER, vertexShaderId), -1);
            }

            // Compile fragment shader
            fragmentShaderId = compileShader(GL20.GL_FRAGMENT_SHADER, fragmentSource, "fragment");
            if (fragmentShaderId == 0) {
                disposeInternal();
                return new ShaderCompilationResult(false, extractLog(GL20.GL_FRAGMENT_SHADER, fragmentShaderId), -1);
            }

            // Link program
            programId = GL20.glCreateProgram();
            if (programId == 0) {
                disposeInternal();
                return new ShaderCompilationResult(false, "Failed to create GL program", -1);
            }

            GL20.glAttachShader(programId, vertexShaderId);
            GL20.glAttachShader(programId, fragmentShaderId);

            // Bind standard attributes (matches Minecraft's DefaultVertexFormat)
            GL20.glBindAttribLocation(programId, 0, "Position");
            GL20.glBindAttribLocation(programId, 1, "UV0");
            GL20.glBindAttribLocation(programId, 2, "Color");
            GL20.glBindAttribLocation(programId, 3, "UV2");
            GL20.glBindAttribLocation(programId, 4, "Normal");

            GL20.glLinkProgram(programId);

            // Check link status
            if (GL20.glGetProgrami(programId, GL20.GL_LINK_STATUS) == GL11.GL_FALSE) {
                String log = GL20.glGetProgramInfoLog(programId, 4096);
                disposeInternal();
                return new ShaderCompilationResult(false, "Link error: " + log, -1);
            }

            // Validate program
            GL20.glValidateProgram(programId);
            if (GL20.glGetProgrami(programId, GL20.GL_VALIDATE_STATUS) == GL11.GL_FALSE) {
                String log = GL20.glGetProgramInfoLog(programId, 4096);
                JujutsuSorcery.LOGGER.warn("Shader '{}' validation warning: {}", name, log);
                // Non-fatal: many shaders work fine despite validation warnings
            }

            // Cache uniform locations
            cacheUniforms();

            active = true;
            disposed = false;
            compileTime = System.nanoTime() - startTime;

            JujutsuSorcery.LOGGER.info(
                    "Shader '{}' compiled in {} ms ({} uniforms)",
                    name, compileTime / 1_000_000.0, uniformCount
            );

            return new ShaderCompilationResult(true, "OK", -1);

        } catch (Exception e) {
            disposeInternal();
            return new ShaderCompilationResult(false, "Exception: " + e.getMessage(), -1);
        }
    }

    /**
     * Compile a single shader stage (vertex or fragment).
     */
    private int compileShader(int type, String source, String stageName) {
        int shaderId = GL20.glCreateShader(type);
        if (shaderId == 0) {
            return 0;
        }

        GL20.glShaderSource(shaderId, source);
        GL20.glCompileShader(shaderId);

        if (GL20.glGetShaderi(shaderId, GL20.GL_COMPILE_STATUS) == GL11.GL_FALSE) {
            String log = GL20.glGetShaderInfoLog(shaderId, 4096);
            int errorLine = parseErrorLine(log);
            JujutsuSorcery.LOGGER.error(
                    "Shader '{}' {} compilation failed [line {}]: {}",
                    name, stageName, errorLine, log
            );
            GL20.glDeleteShader(shaderId);
            return 0;
        }

        return shaderId;
    }

    /**
     * Extract error log from a failed shader stage.
     */
    private String extractLog(int type, int shaderId) {
        if (shaderId == 0) return "Unknown error";
        return GL20.glGetShaderInfoLog(shaderId, 4096);
    }

    /**
     * Parse error line number from GLSL compiler log.
     * GLSL errors typically format as: "0(42) : error C0000: ..."
     */
    private int parseErrorLine(String log) {
        if (log == null || log.isEmpty()) return -1;
        try {
            java.util.regex.Pattern p = java.util.regex.Pattern.compile("\\d*\\((\\d+)\\)|:(\\d+):");
            java.util.regex.Matcher m = p.matcher(log);
            if (m.find()) {
                String g1 = m.group(1);
                String g2 = m.group(2);
                return Integer.parseInt(g1 != null ? g1 : g2);
            }
        } catch (Exception ignored) {}
        return -1;
    }

    /**
     * Cache all active uniform locations for fast updates.
     */
    private void cacheUniforms() {
        uniforms.clear();
        int count = GL20.glGetProgrami(programId, GL20.GL_ACTIVE_UNIFORMS);
        uniformCount = count;

        // Use a single stack frame outside the loop for maximum efficiency
        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer sizeBuffer = stack.mallocInt(1);
            IntBuffer typeBuffer = stack.mallocInt(1);

            for (int i = 0; i < count; i++) {
                // Clear or rewind is not strictly needed for glGetActiveUniform,
                // but it ensures native pointers overwrite from position 0.
                sizeBuffer.rewind();
                typeBuffer.rewind();

                String name = GL20.glGetActiveUniform(programId, i, 256, sizeBuffer, typeBuffer);

                if (name != null && !name.startsWith("gl_")) {
                    int location = GL20.glGetUniformLocation(programId, name);
                    if (location >= 0) {
                        int type = typeBuffer.get(0);
                        uniforms.put(name, new UniformHandle(location, type));
                    }
                }
            }
        }
    }

    // ============================================================
    // UNIFORM MANAGEMENT
    // ============================================================

    public UniformHandle getUniform(String name) {
        return uniforms.get(name);
    }

    public boolean hasUniform(String name) {
        return uniforms.containsKey(name);
    }

    public void setUniformFloat(String name, float value) {
        UniformHandle u = uniforms.get(name);
        if (u != null) u.setFloat(value);
    }

    public void setUniformInt(String name, int value) {
        UniformHandle u = uniforms.get(name);
        if (u != null) u.setInt(value);
    }

    public void setUniformBool(String name, boolean value) {
        UniformHandle u = uniforms.get(name);
        if (u != null) u.setBool(value);
    }

    public void setUniformVec2(String name, float x, float y) {
        UniformHandle u = uniforms.get(name);
        if (u != null) u.setVec2(x, y);
    }

    public void setUniformVec3(String name, float x, float y, float z) {
        UniformHandle u = uniforms.get(name);
        if (u != null) u.setVec3(x, y, z);
    }

    public void setUniformVec4(String name, float x, float y, float z, float w) {
        UniformHandle u = uniforms.get(name);
        if (u != null) u.setVec4(x, y, z, w);
    }

    /**
     * Bind this shader program for rendering.
     */
    public void bind() {
        if (programId != 0) {
            GL20.glUseProgram(programId);
        }
    }

    /**
     * Unbind this shader (restore default).
     */
    public void unbind() {
        GL20.glUseProgram(0);
    }

    // ============================================================
    // LIFECYCLE
    // ============================================================

    public boolean isActive() {
        return active && !disposed && programId != 0;
    }

    public boolean isDisposed() {
        return disposed;
    }

    public void reset() {
        active = false;
    }

    /**
     * Full cleanup of OpenGL resources.
     * Called before recompilation and on mod shutdown.
     */
    public void dispose() {
        disposeInternal();
        disposed = true;
        active = false;
    }

    private void disposeInternal() {
        if (programId != 0) {
            if (vertexShaderId != 0) {
                GL20.glDetachShader(programId, vertexShaderId);
            }
            if (fragmentShaderId != 0) {
                GL20.glDetachShader(programId, fragmentShaderId);
            }
            GL20.glDeleteProgram(programId);
            programId = 0;
        }
        if (vertexShaderId != 0) {
            GL20.glDeleteShader(vertexShaderId);
            vertexShaderId = 0;
        }
        if (fragmentShaderId != 0) {
            GL20.glDeleteShader(fragmentShaderId);
            fragmentShaderId = 0;
        }
        uniforms.clear();
    }

    // ============================================================
    // BENCHMARKING
    // ============================================================

    public void benchmark() {
        if (!isActive()) return;

        // Run 1000 bind/unbind cycles to measure overhead
        long start = System.nanoTime();
        for (int i = 0; i < 1000; i++) {
            bind();
            // Set a dummy uniform to simulate real usage
            setUniformFloat("Time", i * 0.001f);
            unbind();
        }
        long elapsed = System.nanoTime() - start;

        JujutsuSorcery.LOGGER.info(
                "Shader '{}' benchmark: {} ms for 1000 cycles ({} ns/call)",
                name, elapsed / 1_000_000.0, elapsed / 1000
        );
    }

    // ============================================================
    // GETTERS / SETTERS
    // ============================================================

    public String getName() { return name; }

    public String getVertexSource() { return vertexSource; }
    public void setVertexSource(String source) { this.vertexSource = source; }

    public String getFragmentSource() { return fragmentSource; }
    public void setFragmentSource(String source) { this.fragmentSource = source; }

    public int getProgramId() { return programId; }
    public int getUniformCount() { return uniformCount; }
    public long getCompileTime() { return compileTime; }
    public Map<String, UniformHandle> getUniforms() { return new HashMap<>(uniforms); }

    @Override
    public String toString() {
        return String.format("CustomShaderInstance[%s, program=%d, uniforms=%d, active=%s]",
                name, programId, uniformCount, active);
    }
}
