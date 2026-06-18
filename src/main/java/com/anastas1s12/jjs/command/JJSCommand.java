package com.anastas1s12.jjs.command;

import com.anastas1s12.jjs.system.technique.PlayerTechniqueData;
import com.anastas1s12.jjs.system.technique.Technique;
import com.anastas1s12.jjs.system.technique.TechniqueRegistry;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import java.util.concurrent.CompletableFuture;

/**
 * Registers the {@code /jjs} command tree.
 *
 * Current sub-commands
 * --------------------
 *
 *   /jjs technique assign <technique_id> <player>
 *       Assigns the given innate technique to the target player.
 *       Saves the technique ID and the default ability hotbar to the
 *       player's PersistentData (NBT).  Syncs both to the client
 *       immediately so the Abilities menu and sorcerer hotbar update live.
 *
 *       technique_id is tab-completed from all registered technique IDs.
 *       Requires operator permission level 2.
 *
 * Adding more sub-commands
 * ------------------------
 *   Nest additional {@code .then(Commands.literal(...))} branches under the
 *   "technique" literal, or add new top-level literals under "jjs".
 */
public class JJSCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
            Commands.literal("jjs")
                .requires(source -> source.hasPermission(2)) // OP level 2

                // /jjs technique ...
                .then(Commands.literal("technique")

                    // /jjs technique assign <technique_id> <player>
                    .then(Commands.literal("assign")
                        .then(Commands.argument("technique_id", StringArgumentType.word())
                            .suggests(JJSCommand::suggestTechniqueIds)   // ← tab-complete
                            .then(Commands.argument("player", EntityArgument.player())
                                .executes(JJSCommand::executeAssign)
                            )
                        )
                    )

                    // /jjs technique info <player>
                    // Shows which technique is currently assigned.
                    .then(Commands.literal("info")
                        .then(Commands.argument("player", EntityArgument.player())
                            .executes(JJSCommand::executeInfo)
                        )
                    )

                    // /jjs technique clear <player>
                    // Removes the technique and empties the ability hotbar.
                    .then(Commands.literal("clear")
                        .then(Commands.argument("player", EntityArgument.player())
                            .executes(JJSCommand::executeClear)
                        )
                    )
                )
        );
    }

    // =========================================================================
    // Tab-completion: suggest all registered technique IDs
    // =========================================================================

    /**
     * Provides technique ID suggestions for the {@code <technique_id>} argument.
     * All IDs registered in {@link TechniqueRegistry} are offered; the
     * brigadier framework filters them by the prefix the player has already typed.
     */
    private static CompletableFuture<Suggestions> suggestTechniqueIds(
            CommandContext<CommandSourceStack> ctx, SuggestionsBuilder builder) {

        String typed = builder.getRemaining().toLowerCase();
        for (String id : TechniqueRegistry.getIds()) {
            if (id.toLowerCase().startsWith(typed)) {
                builder.suggest(id);
            }
        }
        return builder.buildFuture();
    }

    // =========================================================================
    // /jjs technique assign <technique_id> <player>
    // =========================================================================

    private static int executeAssign(CommandContext<CommandSourceStack> ctx) {
        try {
            String techniqueId = StringArgumentType.getString(ctx, "technique_id");
            ServerPlayer target = EntityArgument.getPlayer(ctx, "player");

            // Validate the technique ID
            Technique technique = TechniqueRegistry.get(techniqueId);
            if (technique == null) {
                ctx.getSource().sendFailure(Component.literal(
                        "Unknown technique: '" + techniqueId + "'. " +
                        "Valid IDs: " + String.join(", ", TechniqueRegistry.getIds())
                ));
                return 0;
            }

            // Assign — saves NBT and syncs both packets to the client
            PlayerTechniqueData.assignTechnique(target, technique);

            // Feedback to the command sender
            ctx.getSource().sendSuccess(() -> Component.literal(
                    "Assigned technique ")
                    .append(technique.getDisplayComponent())
                    .append(Component.literal(" to " + target.getName().getString() + ".")),
                    true);

            // Notify the target player
            target.sendSystemMessage(Component.literal(
                    "\u00A7aYour innate technique has been set to: ")
                    .append(technique.getDisplayComponent()));

            return 1;

        } catch (Exception e) {
            ctx.getSource().sendFailure(Component.literal("Error: " + e.getMessage()));
            return 0;
        }
    }

    // =========================================================================
    // /jjs technique info <player>
    // =========================================================================

    private static int executeInfo(CommandContext<CommandSourceStack> ctx) {
        try {
            ServerPlayer target = EntityArgument.getPlayer(ctx, "player");
            Technique current   = PlayerTechniqueData.getTechnique(target);

            if (current == null) {
                ctx.getSource().sendSuccess(() -> Component.literal(
                        target.getName().getString() + " has no technique assigned."), false);
            } else {
                ctx.getSource().sendSuccess(() -> Component.literal(
                        target.getName().getString() + "'s technique: ")
                        .append(current.getDisplayComponent())
                        .append(Component.literal(
                                " — abilities: " + String.join(", ", current.getAbilityIds()))),
                        false);
            }
            return 1;

        } catch (Exception e) {
            ctx.getSource().sendFailure(Component.literal("Error: " + e.getMessage()));
            return 0;
        }
    }

    // =========================================================================
    // /jjs technique clear <player>
    // =========================================================================

    private static int executeClear(CommandContext<CommandSourceStack> ctx) {
        try {
            ServerPlayer target = EntityArgument.getPlayer(ctx, "player");

            PlayerTechniqueData.assignTechnique(target, null); // null = clear

            ctx.getSource().sendSuccess(() -> Component.literal(
                    "Cleared technique for " + target.getName().getString() + "."), true);

            target.sendSystemMessage(Component.literal(
                    "\u00A77Your innate technique has been removed."));

            return 1;

        } catch (Exception e) {
            ctx.getSource().sendFailure(Component.literal("Error: " + e.getMessage()));
            return 0;
        }
    }
}
