package com.anastas1s12.jjs.command;

import com.anastas1s12.jjs.system.shaders.ShaderEditorScreen;
import com.anastas1s12.jjs.system.shaders.ShaderManager;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

public class DebugCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal("debug")
                        .requires(source -> source.hasPermission(2))
                        .then(Commands.literal("shader_editor")
                                .executes(context -> {
                                    Minecraft.getInstance().setScreen(new ShaderEditorScreen());
                                    ShaderManager.loadImpactShader();
                                    return 1;
                                })
                        )
        );
    }
}
