package com.anastas1s12.jjs.config;

import net.minecraftforge.common.ForgeConfigSpec;

/**
 * ShaderWorkbench Configuration
 *
 * Client-side configuration for the mod.
 * Includes keybind preferences, performance settings, and debug options.
 */
public class SWBConfig {
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;

    // Keybind settings
    public static final ForgeConfigSpec.ConfigValue<String> KEY_OPEN_WORKBENCH;
    public static final ForgeConfigSpec.ConfigValue<String> KEY_DEBUG_OVERLAY;

    // Performance settings
    public static final ForgeConfigSpec.BooleanValue ASYNC_COMPILATION;
    public static final ForgeConfigSpec.IntValue MAX_SHADER_PASSES;
    public static final ForgeConfigSpec.BooleanValue AUTO_RELOAD;
    public static final ForgeConfigSpec.IntValue RELOAD_DELAY_MS;

    // Debug settings
    public static final ForgeConfigSpec.BooleanValue SHOW_COMPILE_LOG;
    public static final ForgeConfigSpec.BooleanValue SHOW_GPU_STATS;
    public static final ForgeConfigSpec.BooleanValue ENABLE_BENCHMARK;

    // Editor settings
    public static final ForgeConfigSpec.BooleanValue SYNTAX_HIGHLIGHTING;
    public static final ForgeConfigSpec.BooleanValue AUTO_INDENT;
    public static final ForgeConfigSpec.BooleanValue LINE_NUMBERS;
    public static final ForgeConfigSpec.IntValue FONT_SIZE;

    // Impact frame settings
    public static final ForgeConfigSpec.BooleanValue IMPACT_FRAMES_ENABLED;
    public static final ForgeConfigSpec.IntValue MAX_CONCURRENT_EFFECTS;

    static {
        BUILDER.push("keybinds");
        KEY_OPEN_WORKBENCH = BUILDER
                .comment("Key to open the Shader Workbench GUI (default: F8)")
                .define("openWorkbench", "key.keyboard.f8");
        KEY_DEBUG_OVERLAY = BUILDER
                .comment("Key to toggle debug overlay (default: F9)")
                .define("debugOverlay", "key.keyboard.f9");
        BUILDER.pop();

        BUILDER.push("performance");
        ASYNC_COMPILATION = BUILDER
                .comment("Enable async shader compilation")
                .define("asyncCompilation", true);
        MAX_SHADER_PASSES = BUILDER
                .comment("Maximum number of shader passes in the pipeline")
                .defineInRange("maxShaderPasses", 16, 1, 32);
        AUTO_RELOAD = BUILDER
                .comment("Auto-reload shaders on file change")
                .define("autoReload", true);
        RELOAD_DELAY_MS = BUILDER
                .comment("Delay before auto-reloading shaders (ms)")
                .defineInRange("reloadDelayMs", 500, 100, 5000);
        BUILDER.pop();

        BUILDER.push("debug");
        SHOW_COMPILE_LOG = BUILDER
                .comment("Show shader compilation log in console")
                .define("showCompileLog", true);
        SHOW_GPU_STATS = BUILDER
                .comment("Show GPU statistics in debug overlay")
                .define("showGpuStats", true);
        ENABLE_BENCHMARK = BUILDER
                .comment("Enable shader benchmarking mode")
                .define("enableBenchmark", false);
        BUILDER.pop();

        BUILDER.push("editor");
        SYNTAX_HIGHLIGHTING = BUILDER
                .comment("Enable GLSL syntax highlighting")
                .define("syntaxHighlighting", true);
        AUTO_INDENT = BUILDER
                .comment("Enable auto-indentation")
                .define("autoIndent", true);
        LINE_NUMBERS = BUILDER
                .comment("Show line numbers in editor")
                .define("lineNumbers", true);
        FONT_SIZE = BUILDER
                .comment("Editor font size")
                .defineInRange("fontSize", 12, 8, 24);
        BUILDER.pop();

        BUILDER.push("impact");
        IMPACT_FRAMES_ENABLED = BUILDER
                .comment("Enable impact frame effects")
                .define("enabled", true);
        MAX_CONCURRENT_EFFECTS = BUILDER
                .comment("Maximum concurrent impact effects")
                .defineInRange("maxConcurrentEffects", 8, 1, 32);
        BUILDER.pop();

        SPEC = BUILDER.build();
    }
}
