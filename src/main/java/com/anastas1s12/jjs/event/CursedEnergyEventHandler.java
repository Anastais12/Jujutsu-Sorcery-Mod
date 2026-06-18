package com.anastas1s12.jjs.event;

import com.anastas1s12.jjs.JujutsuSorcery;
import com.anastas1s12.jjs.capability.CursedEnergyCapability;
import com.anastas1s12.jjs.capability.ICursedEnergy;
import com.anastas1s12.jjs.networking.s2c.CursedEnergySyncS2CPacket;
import com.anastas1s12.jjs.networking.ModNetworking;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import com.anastas1s12.jjs.networking.s2c.SyncAbilityHotbarS2CPacket;
import net.minecraftforge.network.PacketDistributor;

/**
 * Server-side event handler for Cursed Energy mechanics.
 * Handles: per-tick regen, CE output damage modification, player sync on login/respawn.
 */
@Mod.EventBusSubscriber(modid = JujutsuSorcery.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CursedEnergyEventHandler {

    private static final int SYNC_INTERVAL_TICKS = 5; // Sync every 5 ticks (4 times/sec)

    // Player Tick - Regeneration & Periodic Sync
    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;

        Player player = event.player;
        if (player.level().isClientSide) return; // Only server side

        player.getCapability(CursedEnergyCapability.CURSED_ENERGY_CAPABILITY).ifPresent(ce -> {
            // Run passive regeneration
            ce.onTick();

            // RCT healing if active
            if (ce.isRCTActive() && ce.getCurrentCE() > 0) {
                handleRCTHealing(player, ce);
            }

            // Periodic sync to client
            if (player.tickCount % SYNC_INTERVAL_TICKS == 0) {
                syncToClient((ServerPlayer) player, ce);
            }
        });
    }

    // Combat - CE Output Damage
    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        // Apply CE Output to player melee damage
        if (event.getSource().getEntity() instanceof Player player) {
            player.getCapability(CursedEnergyCapability.CURSED_ENERGY_CAPABILITY).ifPresent(ce -> {
                // Only boost physical/melee damage, not environmental
                if (isMeleeDamage(event)) {
                    float original = event.getAmount();
                    float modified = ce.getEffectiveDamage(original);

                    // Small CE cost for boosting damage (costs 1-3 CE per hit)
                    float boostCost = Math.max(1.0f, modified - original);
                    if (ce.consume(boostCost)) {
                        event.setAmount(modified);
                    }
                    // If not enough CE, damage stays unboosted
                }
            });
        }
    }

    // Player Login / Respawn / Dimension Change - Sync
    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            player.getCapability(CursedEnergyCapability.CURSED_ENERGY_CAPABILITY).ifPresent(ce -> {
                syncToClient(player, ce);
            });
            // Sync ability hotbar assignments to the newly connected client.
            // TODO: load per-player hotbar data from capability / saved data.
            //       For now sends an empty hotbar so the client initialises cleanly.
            String[] slots = loadAbilityHotbar(player);
            ModNetworking.INSTANCE.send(
                    PacketDistributor.PLAYER.with(() -> player),
                    new SyncAbilityHotbarS2CPacket(slots)
            );
        }
    }

    @SubscribeEvent
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            player.getCapability(CursedEnergyCapability.CURSED_ENERGY_CAPABILITY).ifPresent(ce -> {
                // Refill CE on respawn (partial)
                ce.setCurrentCE(ce.getMaxCE() * 0.5f);
                syncToClient(player, ce);
            });
        }
    }

    @SubscribeEvent
    public static void onPlayerChangedDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            player.getCapability(CursedEnergyCapability.CURSED_ENERGY_CAPABILITY).ifPresent(ce -> {
                syncToClient(player, ce);
            });
        }
    }

    // Helper Methods
    private static void handleRCTHealing(Player player, ICursedEnergy ce) {
        // RCT costs 20 CE per second (1 CE per tick at 20tps)
        float rctCostPerTick = 1.0f;

        if (ce.getCurrentCE() >= rctCostPerTick && player.getHealth() < player.getMaxHealth()) {
            ce.consume(rctCostPerTick * 20); // Display cost is per second, so we adjust
            // Heal 1 heart (2 HP) per second = 0.1 HP per tick
            player.heal(0.1f);
        } else if (ce.getCurrentCE() < rctCostPerTick) {
            // Auto-disable RCT if out of CE
            ce.setRCTActive(false);
        }
    }

    private static boolean isMeleeDamage(LivingHurtEvent event) {
        DamageSource source = event.getSource();

        // Check if this is a player melee attack
        return source.getDirectEntity() == source.getEntity()
                && !source.is(DamageTypeTags.IS_PROJECTILE)
                && !source.is(DamageTypeTags.IS_EXPLOSION)
                && !source.is(DamageTypeTags.BYPASSES_ARMOR); // Use BYPASSES_ARMOR or WITHER for old 'isMagic' intent
    }

    public static void syncToClient(ServerPlayer player, ICursedEnergy ce) {
        ModNetworking.INSTANCE.send(
                PacketDistributor.PLAYER.with(() -> player),
                new CursedEnergySyncS2CPacket(ce)
        );
    }

    /**
     * Loads the player's saved ability hotbar from persistent data.
     * Slots are stored as "jjs_hotbar_0" … "jjs_hotbar_8".
     * Returns an array of 9 strings; empty string = unoccupied slot.
     *
     * TODO: replace with a proper capability or SavedData when the ability
     *       persistence system is implemented.
     */
    private static String[] loadAbilityHotbar(ServerPlayer player) {
        String[] slots = new String[9];
        for (int i = 0; i < 9; i++) {
            slots[i] = player.getPersistentData().getString("jjs_hotbar_" + i);
        }
        return slots;
    }
}
