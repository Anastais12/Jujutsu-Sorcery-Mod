package com.anastas1s12.jjs.command;

import com.anastas1s12.jjs.capability.CursedEnergy;
import com.anastas1s12.jjs.capability.CursedEnergyCapability;
import com.anastas1s12.jjs.capability.ICursedEnergy;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.Collection;
import java.util.Collections;

/**
 * Debug/admin commands for managing Cursed Energy.
 * Usage:
 * /ce get [player]           - View CE stats
 * /ce set <value> [player]   - Set current CE
 * /ce add <value> [player]   - Add to current CE
 * /ce max <value> [player]   - Set base max CE
 * /ce regen <value> [player] - Set regen rate
 * /ce eff <value> [player]   - Set efficiency (0-1)
 * /ce output <value> [player]- Set output multiplier
 * /ce mastery <value> [player] - Set mastery level
 * /ce points <value> [player]- Set mastery points
 * /ce reset [player]         - Reset all CE data
 * /ce fill [player]          - Fill CE to max
 * /ce finger [player]        - Add one Sukuna finger
 */
public class CECommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("ce")
                .requires(source -> source.hasPermission(2)) // Requires OP level 2

                // /ce get [player]
                .then(Commands.literal("get")
                        .executes(ctx -> getCE(ctx, Collections.singleton(ctx.getSource().getPlayerOrException())))
                        .then(Commands.argument("player", EntityArgument.player())
                                .executes(ctx -> getCE(ctx, Collections.singleton(EntityArgument.getPlayer(ctx, "player"))))
                        )
                )

                // /ce set <value> [player]
                .then(Commands.literal("set")
                        .then(Commands.argument("value", FloatArgumentType.floatArg(0))
                                .executes(ctx -> setCE(ctx, Collections.singleton(ctx.getSource().getPlayerOrException()), FloatArgumentType.getFloat(ctx, "value")))
                                .then(Commands.argument("player", EntityArgument.players())
                                        .executes(ctx -> setCE(ctx, EntityArgument.getPlayers(ctx, "player"), FloatArgumentType.getFloat(ctx, "value")))
                                )
                        )
                )

                // /ce add <value> [player]
                .then(Commands.literal("add")
                        .then(Commands.argument("value", FloatArgumentType.floatArg())
                                .executes(ctx -> addCE(ctx, Collections.singleton(ctx.getSource().getPlayerOrException()), FloatArgumentType.getFloat(ctx, "value")))
                                .then(Commands.argument("player", EntityArgument.players())
                                        .executes(ctx -> addCE(ctx, EntityArgument.getPlayers(ctx, "player"), FloatArgumentType.getFloat(ctx, "value")))
                                )
                        )
                )

                // /ce max <value> [player]
                .then(Commands.literal("max")
                        .then(Commands.argument("value", FloatArgumentType.floatArg(50))
                                .executes(ctx -> setMaxCE(ctx, Collections.singleton(ctx.getSource().getPlayerOrException()), FloatArgumentType.getFloat(ctx, "value")))
                                .then(Commands.argument("player", EntityArgument.players())
                                        .executes(ctx -> setMaxCE(ctx, EntityArgument.getPlayers(ctx, "player"), FloatArgumentType.getFloat(ctx, "value")))
                                )
                        )
                )

                // /ce regen <value> [player]
                .then(Commands.literal("regen")
                        .then(Commands.argument("value", FloatArgumentType.floatArg(0))
                                .executes(ctx -> setRegen(ctx, Collections.singleton(ctx.getSource().getPlayerOrException()), FloatArgumentType.getFloat(ctx, "value")))
                                .then(Commands.argument("player", EntityArgument.players())
                                        .executes(ctx -> setRegen(ctx, EntityArgument.getPlayers(ctx, "player"), FloatArgumentType.getFloat(ctx, "value")))
                                )
                        )
                )

                // /ce eff <value> [player]
                .then(Commands.literal("eff")
                        .then(Commands.argument("value", FloatArgumentType.floatArg(0, 0.95f))
                                .executes(ctx -> setEfficiency(ctx, Collections.singleton(ctx.getSource().getPlayerOrException()), FloatArgumentType.getFloat(ctx, "value")))
                                .then(Commands.argument("player", EntityArgument.players())
                                        .executes(ctx -> setEfficiency(ctx, EntityArgument.getPlayers(ctx, "player"), FloatArgumentType.getFloat(ctx, "value")))
                                )
                        )
                )

                // /ce output <value> [player]
                .then(Commands.literal("output")
                        .then(Commands.argument("value", FloatArgumentType.floatArg(0.1f))
                                .executes(ctx -> setOutput(ctx, Collections.singleton(ctx.getSource().getPlayerOrException()), FloatArgumentType.getFloat(ctx, "value")))
                                .then(Commands.argument("player", EntityArgument.players())
                                        .executes(ctx -> setOutput(ctx, EntityArgument.getPlayers(ctx, "player"), FloatArgumentType.getFloat(ctx, "value")))
                                )
                        )
                )

                // /ce mastery <value> [player]
                .then(Commands.literal("mastery")
                        .then(Commands.argument("value", IntegerArgumentType.integer(0))
                                .executes(ctx -> setMastery(ctx, Collections.singleton(ctx.getSource().getPlayerOrException()), IntegerArgumentType.getInteger(ctx, "value")))
                                .then(Commands.argument("player", EntityArgument.players())
                                        .executes(ctx -> setMastery(ctx, EntityArgument.getPlayers(ctx, "player"), IntegerArgumentType.getInteger(ctx, "value")))
                                )
                        )
                )

                // /ce points <value> [player]
                .then(Commands.literal("points")
                        .then(Commands.argument("value", IntegerArgumentType.integer(0))
                                .executes(ctx -> setMasteryPoints(ctx, Collections.singleton(ctx.getSource().getPlayerOrException()), IntegerArgumentType.getInteger(ctx, "value")))
                                .then(Commands.argument("player", EntityArgument.players())
                                        .executes(ctx -> setMasteryPoints(ctx, EntityArgument.getPlayers(ctx, "player"), IntegerArgumentType.getInteger(ctx, "value")))
                                )
                        )
                )

                // /ce reset [player]
                .then(Commands.literal("reset")
                        .executes(ctx -> resetCE(ctx, Collections.singleton(ctx.getSource().getPlayerOrException())))
                        .then(Commands.argument("player", EntityArgument.players())
                                .executes(ctx -> resetCE(ctx, EntityArgument.getPlayers(ctx, "player")))
                        )
                )

                // /ce fill [player]
                .then(Commands.literal("fill")
                        .executes(ctx -> fillCE(ctx, Collections.singleton(ctx.getSource().getPlayerOrException())))
                        .then(Commands.argument("player", EntityArgument.players())
                                .executes(ctx -> fillCE(ctx, EntityArgument.getPlayers(ctx, "player")))
                        )
                )

                // /ce finger [player]
                .then(Commands.literal("finger")
                        .executes(ctx -> addFinger(ctx, Collections.singleton(ctx.getSource().getPlayerOrException())))
                        .then(Commands.argument("player", EntityArgument.players())
                                .executes(ctx -> addFinger(ctx, EntityArgument.getPlayers(ctx, "player")))
                        )
                )
        );
    }

    // ============================================================
    // Command Implementations
    // ============================================================

    private static int getCE(CommandContext<CommandSourceStack> ctx, Collection<? extends Player> players) {
        for (Player player : players) {
            player.getCapability(CursedEnergyCapability.CURSED_ENERGY_CAPABILITY).ifPresent(ce -> {
                ctx.getSource().sendSuccess(() -> Component.literal(
                        String.format("\u00A7a=== %s's Cursed Energy ===", player.getName().getString())
                ), false);
                ctx.getSource().sendSuccess(() -> Component.literal(
                        String.format("CE: \u00A7b%.1f\u00A7f / \u00A7b%.1f\u00A7f (%.1f%%)",
                                ce.getCurrentCE(), ce.getMaxCE(), ce.getCERatio() * 100)
                ), false);
                ctx.getSource().sendSuccess(() -> Component.literal(
                        String.format("Base Max: %.1f | Regen: %.3f/tick | Eff: %.1f%% | Output: %.2fx",
                                ce.getBaseMaxCE(), ce.getRegenRate(), ce.getEfficiency() * 100, ce.getOutput())
                ), false);
                ctx.getSource().sendSuccess(() -> Component.literal(
                        String.format("Mastery: %d (XP: %d/%d) | Points: %d | Fingers: %d",
                                ce.getMasteryLevel(), ce.getMasteryXP(), CursedEnergy.MASTERY_XP_PER_LEVEL,
                                ce.getMasteryPoints(), ce.getFingersConsumed())
                ), false);
                ctx.getSource().sendSuccess(() -> Component.literal(
                        String.format("RCT: %s | Six Eyes: %s | Channeling: %s",
                                ce.isRCTActive() ? "\u00A7dON" : "\u00A77OFF",
                                ce.hasSixEyes() ? "\u00A7bYES" : "\u00A77NO",
                                ce.isChanneling() ? "\u00A7cYES" : "\u00A77NO")
                ), false);
            });
        }
        return 1;
    }

    private static int setCE(CommandContext<CommandSourceStack> ctx, Collection<? extends Player> players, float value) {
        for (Player player : players) {
            player.getCapability(CursedEnergyCapability.CURSED_ENERGY_CAPABILITY).ifPresent(ce -> {
                ce.setCurrentCE(value);
                ctx.getSource().sendSuccess(() -> Component.literal(
                        String.format("Set %s's CE to %.1f", player.getName().getString(), ce.getCurrentCE())
                ), true);
            });
        }
        return 1;
    }

    private static int addCE(CommandContext<CommandSourceStack> ctx, Collection<? extends Player> players, float value) {
        for (Player player : players) {
            player.getCapability(CursedEnergyCapability.CURSED_ENERGY_CAPABILITY).ifPresent(ce -> {
                ce.add(value);
                ctx.getSource().sendSuccess(() -> Component.literal(
                        String.format("Added %.1f CE to %s (now %.1f/%.1f)",
                                value, player.getName().getString(), ce.getCurrentCE(), ce.getMaxCE())
                ), true);
            });
        }
        return 1;
    }

    private static int setMaxCE(CommandContext<CommandSourceStack> ctx, Collection<? extends Player> players, float value) {
        for (Player player : players) {
            player.getCapability(CursedEnergyCapability.CURSED_ENERGY_CAPABILITY).ifPresent(ce -> {
                ce.setBaseMaxCE(value);
                ctx.getSource().sendSuccess(() -> Component.literal(
                        String.format("Set %s's base Max CE to %.1f (total max: %.1f)",
                                player.getName().getString(), value, ce.getMaxCE())
                ), true);
            });
        }
        return 1;
    }

    private static int setRegen(CommandContext<CommandSourceStack> ctx, Collection<? extends Player> players, float value) {
        for (Player player : players) {
            player.getCapability(CursedEnergyCapability.CURSED_ENERGY_CAPABILITY).ifPresent(ce -> {
                ce.setBaseRegenRate(value);
                ctx.getSource().sendSuccess(() -> Component.literal(
                        String.format("Set %s's regen rate to %.3f CE/tick", player.getName().getString(), value)
                ), true);
            });
        }
        return 1;
    }

    private static int setEfficiency(CommandContext<CommandSourceStack> ctx, Collection<? extends Player> players, float value) {
        for (Player player : players) {
            player.getCapability(CursedEnergyCapability.CURSED_ENERGY_CAPABILITY).ifPresent(ce -> {
                ce.setBaseEfficiency(value);
                ctx.getSource().sendSuccess(() -> Component.literal(
                        String.format("Set %s's efficiency to %.1f%% (costs %.1f%% of base)",
                                player.getName().getString(), value * 100, (1 - value) * 100)
                ), true);
            });
        }
        return 1;
    }

    private static int setOutput(CommandContext<CommandSourceStack> ctx, Collection<? extends Player> players, float value) {
        for (Player player : players) {
            player.getCapability(CursedEnergyCapability.CURSED_ENERGY_CAPABILITY).ifPresent(ce -> {
                ce.setBaseOutput(value);
                ctx.getSource().sendSuccess(() -> Component.literal(
                        String.format("Set %s's CE output to %.2fx damage", player.getName().getString(), value)
                ), true);
            });
        }
        return 1;
    }

    private static int setMastery(CommandContext<CommandSourceStack> ctx, Collection<? extends Player> players, int value) {
        for (Player player : players) {
            player.getCapability(CursedEnergyCapability.CURSED_ENERGY_CAPABILITY).ifPresent(ce -> {
                ce.setMasteryLevel(value);
                ctx.getSource().sendSuccess(() -> Component.literal(
                        String.format("Set %s's mastery level to %d", player.getName().getString(), value)
                ), true);
            });
        }
        return 1;
    }

    private static int setMasteryPoints(CommandContext<CommandSourceStack> ctx, Collection<? extends Player> players, int value) {
        for (Player player : players) {
            player.getCapability(CursedEnergyCapability.CURSED_ENERGY_CAPABILITY).ifPresent(ce -> {
                ce.setMasteryPoints(value);
                ctx.getSource().sendSuccess(() -> Component.literal(
                        String.format("Set %s's mastery points to %d", player.getName().getString(), value)
                ), true);
            });
        }
        return 1;
    }

    private static int resetCE(CommandContext<CommandSourceStack> ctx, Collection<? extends Player> players) {
        for (Player player : players) {
            player.getCapability(CursedEnergyCapability.CURSED_ENERGY_CAPABILITY).ifPresent(ce -> {
                ce.setCurrentCE(CursedEnergy.DEFAULT_BASE_MAX_CE);
                ce.setBaseMaxCE(CursedEnergy.DEFAULT_BASE_MAX_CE);
                ce.setBaseRegenRate(CursedEnergy.DEFAULT_BASE_REGEN);
                ce.setBaseEfficiency(CursedEnergy.DEFAULT_BASE_EFFICIENCY);
                ce.setBaseOutput(CursedEnergy.DEFAULT_BASE_OUTPUT);
                ce.setMasteryLevel(0);
                ce.setMasteryPoints(0);
                ce.setFingersConsumed(0);
                ce.setSixEyes(false);
                ce.setRCTActive(false);
                ce.setChanneling(false);
                ce.recalculateMaxCE();
                ctx.getSource().sendSuccess(() -> Component.literal(
                        String.format("Reset %s's Cursed Energy data", player.getName().getString())
                ), true);
            });
        }
        return 1;
    }

    private static int fillCE(CommandContext<CommandSourceStack> ctx, Collection<? extends Player> players) {
        for (Player player : players) {
            player.getCapability(CursedEnergyCapability.CURSED_ENERGY_CAPABILITY).ifPresent(ce -> {
                ce.setCurrentCE(ce.getMaxCE());
                ctx.getSource().sendSuccess(() -> Component.literal(
                        String.format("Filled %s's CE to %.1f", player.getName().getString(), ce.getMaxCE())
                ), true);
            });
        }
        return 1;
    }

    private static int addFinger(CommandContext<CommandSourceStack> ctx, Collection<? extends Player> players) {
        for (Player player : players) {
            player.getCapability(CursedEnergyCapability.CURSED_ENERGY_CAPABILITY).ifPresent(ce -> {
                ce.addFinger();
                ce.addMasteryPoints(CursedEnergy.FINGER_MASTERY_BONUS);
                ctx.getSource().sendSuccess(() -> Component.literal(
                        String.format("Gave %s a Sukuna Finger! (total: %d, Max CE: +%.0f)",
                                player.getName().getString(), ce.getFingersConsumed(), CursedEnergy.FINGER_MAX_CE_BONUS)
                ), true);
            });
        }
        return 1;
    }
}
