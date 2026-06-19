package com.anastas1s12.jjs.command;

import com.anastas1s12.jjs.JujutsuSorcery;
import com.anastas1s12.jjs.system.shader.data.project.ProjectManager;
import com.anastas1s12.jjs.system.shader.render.ShaderRenderManager;
import com.anastas1s12.jjs.system.shader.render.postprocess.PostProcessPipeline;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

/**
 * ShaderWorkbench Shader Commands
 *
 * /shader reload     - Reload all shaders
 * /shader list       - List all loaded shaders
 * /shader enable <name>   - Enable a shader
 * /shader disable <name>  - Disable a shader
 * /shader compile <name>  - Compile a specific shader
 * /shader export <name>   - Export a shader to file
 * /shader import <file>   - Import a shader from file
 * /shader benchmark       - Run performance benchmark
 * /shader debug           - Toggle debug mode
 * /shader reset           - Reset all shaders to default
 */
public class ShaderCommands {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("shader")
                .requires(source -> source.hasPermission(0)) // Allow all players

                .then(Commands.literal("reload")
                        .executes(ShaderCommands::executeReload))

                .then(Commands.literal("list")
                        .executes(ShaderCommands::executeList))

                .then(Commands.literal("enable")
                        .then(Commands.argument("name", StringArgumentType.string())
                                .executes(ShaderCommands::executeEnable)))

                .then(Commands.literal("disable")
                        .then(Commands.argument("name", StringArgumentType.string())
                                .executes(ShaderCommands::executeDisable)))

                .then(Commands.literal("compile")
                        .then(Commands.argument("name", StringArgumentType.string())
                                .executes(ShaderCommands::executeCompile)))

                .then(Commands.literal("export")
                        .then(Commands.argument("name", StringArgumentType.string())
                                .executes(ShaderCommands::executeExport)))

                .then(Commands.literal("import")
                        .then(Commands.argument("file", StringArgumentType.string())
                                .executes(ShaderCommands::executeImport)))

                .then(Commands.literal("benchmark")
                        .executes(ShaderCommands::executeBenchmark))

                .then(Commands.literal("debug")
                        .executes(ShaderCommands::executeDebug))

                .then(Commands.literal("reset")
                        .executes(ShaderCommands::executeReset))
        );
    }

    private static int executeReload(CommandContext<CommandSourceStack> context) {
        ShaderRenderManager manager = JujutsuSorcery.getInstance().getRenderManager();
        if (manager != null) {
            manager.reloadAllShaders();
            context.getSource().sendSuccess(() -> Component.literal("§a[ShaderWorkbench] All shaders reloaded successfully."), false);
        } else {
            context.getSource().sendFailure(Component.literal("§c[ShaderWorkbench] Shader manager not initialized."));
        }
        return 1;
    }

    private static int executeList(CommandContext<CommandSourceStack> context) {
        ProjectManager projectManager = JujutsuSorcery.getInstance().getProjectManager();
        if (projectManager != null) {
            var projects = projectManager.listProjects();
            context.getSource().sendSuccess(() -> Component.literal("§b[ShaderWorkbench] Loaded Shaders:"), false);
            for (String name : projects) {
                boolean enabled = projectManager.isProjectEnabled(name);
                String status = enabled ? "§a[ON]" : "§7[OFF]";
                context.getSource().sendSuccess(() -> Component.literal("  " + status + " §f" + name), false);
            }
            context.getSource().sendSuccess(() -> Component.literal("§7Total: " + projects.size() + " shaders"), false);
        } else {
            context.getSource().sendFailure(Component.literal("§c[ShaderWorkbench] Project manager not initialized."));
        }
        return 1;
    }

    private static int executeEnable(CommandContext<CommandSourceStack> context) {
        String name = StringArgumentType.getString(context, "name");
        PostProcessPipeline pipeline = JujutsuSorcery.getInstance().getPostProcessPipeline();
        if (pipeline != null) {
            pipeline.enableShader(name);
            context.getSource().sendSuccess(() -> Component.literal("§a[ShaderWorkbench] Shader '" + name + "' enabled."), false);
        }
        return 1;
    }

    private static int executeDisable(CommandContext<CommandSourceStack> context) {
        String name = StringArgumentType.getString(context, "name");
        PostProcessPipeline pipeline = JujutsuSorcery.getInstance().getPostProcessPipeline();
        if (pipeline != null) {
            pipeline.disableShader(name);
            context.getSource().sendSuccess(() -> Component.literal("§c[ShaderWorkbench] Shader '" + name + "' disabled."), false);
        }
        return 1;
    }

    private static int executeCompile(CommandContext<CommandSourceStack> context) {
        String name = StringArgumentType.getString(context, "name");
        ShaderRenderManager manager = JujutsuSorcery.getInstance().getRenderManager();
        if (manager != null) {
            boolean success = manager.compileShader(name);
            if (success) {
                context.getSource().sendSuccess(() -> Component.literal("§a[ShaderWorkbench] Shader '" + name + "' compiled successfully."), false);
            } else {
                context.getSource().sendFailure(Component.literal("§c[ShaderWorkbench] Failed to compile shader '" + name + "'. Check logs for details."));
            }
        }
        return 1;
    }

    private static int executeExport(CommandContext<CommandSourceStack> context) {
        String name = StringArgumentType.getString(context, "name");
        ProjectManager projectManager = JujutsuSorcery.getInstance().getProjectManager();
        if (projectManager != null) {
            String path = projectManager.exportProject(name);
            context.getSource().sendSuccess(() -> Component.literal("§a[ShaderWorkbench] Shader '" + name + "' exported to: " + path), false);
        }
        return 1;
    }

    private static int executeImport(CommandContext<CommandSourceStack> context) {
        String file = StringArgumentType.getString(context, "file");
        ProjectManager projectManager = JujutsuSorcery.getInstance().getProjectManager();
        if (projectManager != null) {
            boolean success = projectManager.importProject(file);
            if (success) {
                context.getSource().sendSuccess(() -> Component.literal("§a[ShaderWorkbench] Shader imported from: " + file), false);
            } else {
                context.getSource().sendFailure(Component.literal("§c[ShaderWorkbench] Failed to import shader from: " + file));
            }
        }
        return 1;
    }

    private static int executeBenchmark(CommandContext<CommandSourceStack> context) {
        ShaderRenderManager manager = JujutsuSorcery.getInstance().getRenderManager();
        if (manager != null) {
            manager.runBenchmark();
            context.getSource().sendSuccess(() -> Component.literal("§b[ShaderWorkbench] Benchmark started. Results will be logged."), false);
        }
        return 1;
    }

    private static int executeDebug(CommandContext<CommandSourceStack> context) {
        ShaderRenderManager manager = JujutsuSorcery.getInstance().getRenderManager();
        if (manager != null) {
            manager.toggleDebugMode();
            boolean debug = manager.isDebugMode();
            context.getSource().sendSuccess(() -> Component.literal("§b[ShaderWorkbench] Debug mode: " + (debug ? "§aON" : "§cOFF")), false);
        }
        return 1;
    }

    private static int executeReset(CommandContext<CommandSourceStack> context) {
        ShaderRenderManager manager = JujutsuSorcery.getInstance().getRenderManager();
        PostProcessPipeline pipeline = JujutsuSorcery.getInstance().getPostProcessPipeline();
        if (manager != null) {
            manager.resetAllShaders();
        }
        if (pipeline != null) {
            pipeline.reset();
        }
        context.getSource().sendSuccess(() -> Component.literal("§a[ShaderWorkbench] All shaders reset to default state."), false);
        return 1;
    }
}
