package com.anastas1s12.jjs.command;

import com.anastas1s12.jjs.JujutsuSorcery;
import com.anastas1s12.jjs.system.shader.render.impact.ImpactEffectManager;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

/**
 * ShaderWorkbench Impact Frame Commands
 *
 * /impact test         - Test all impact effects
 * /impact trigger <effect>  - Trigger a specific effect
 * /impact stop         - Stop all running effects
 * /impact list         - List available impact effects
 * /impact debug        - Toggle impact debug mode
 */
public class ImpactCommands {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("impact")
                .requires(source -> source.hasPermission(0))

                .then(Commands.literal("test")
                        .executes(ImpactCommands::executeTest))

                .then(Commands.literal("trigger")
                        .then(Commands.argument("effect", StringArgumentType.string())
                                .executes(ImpactCommands::executeTrigger)))

                .then(Commands.literal("stop")
                        .executes(ImpactCommands::executeStop))

                .then(Commands.literal("list")
                        .executes(ImpactCommands::executeList))

                .then(Commands.literal("debug")
                        .executes(ImpactCommands::executeDebug))
        );
    }

    private static int executeTest(CommandContext<CommandSourceStack> context) {
        ImpactEffectManager manager = JujutsuSorcery.getInstance().getImpactEffectManager();
        if (manager != null) {
            manager.testAllEffects();
            context.getSource().sendSuccess(() -> Component.literal("§a[ShaderWorkbench] Testing all impact effects..."), false);
        }
        return 1;
    }

    private static int executeTrigger(CommandContext<CommandSourceStack> context) {
        String effect = StringArgumentType.getString(context, "effect");
        ImpactEffectManager manager = JujutsuSorcery.getInstance().getImpactEffectManager();
        if (manager != null) {
            boolean success = manager.triggerEffect(effect);
            if (success) {
                context.getSource().sendSuccess(() -> Component.literal("§a[ShaderWorkbench] Triggered effect: " + effect), false);
            } else {
                context.getSource().sendFailure(Component.literal("§c[ShaderWorkbench] Unknown effect: " + effect + ". Use /impact list for available effects."));
            }
        }
        return 1;
    }

    private static int executeStop(CommandContext<CommandSourceStack> context) {
        ImpactEffectManager manager = JujutsuSorcery.getInstance().getImpactEffectManager();
        if (manager != null) {
            manager.stopAllEffects();
            context.getSource().sendSuccess(() -> Component.literal("§c[ShaderWorkbench] All impact effects stopped."), false);
        }
        return 1;
    }

    private static int executeList(CommandContext<CommandSourceStack> context) {
        ImpactEffectManager manager = JujutsuSorcery.getInstance().getImpactEffectManager();
        if (manager != null) {
            var effects = manager.getAvailableEffects();
            context.getSource().sendSuccess(() -> Component.literal("§b[ShaderWorkbench] Available Impact Effects:"), false);
            for (String effect : effects) {
                context.getSource().sendSuccess(() -> Component.literal("  §f- " + effect), false);
            }
        }
        return 1;
    }

    private static int executeDebug(CommandContext<CommandSourceStack> context) {
        ImpactEffectManager manager = JujutsuSorcery.getInstance().getImpactEffectManager();
        if (manager != null) {
            manager.toggleDebug();
            boolean debug = manager.isDebugMode();
            context.getSource().sendSuccess(() -> Component.literal("§b[ShaderWorkbench] Impact debug: " + (debug ? "§aON" : "§cOFF")), false);
        }
        return 1;
    }
}
