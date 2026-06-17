package com.anastas1s12.jjs.capability;

import com.anastas1s12.jjs.JujutsuSorcery;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Handles registration and attachment of the CursedEnergy capability.
 * All events are static and subscribed to the Forge event bus.
 */
@Mod.EventBusSubscriber(modid = JujutsuSorcery.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CursedEnergyCapability {

    public static final Capability<ICursedEnergy> CURSED_ENERGY_CAPABILITY =
            CapabilityManager.get(new CapabilityToken<>() {});

    public static final ResourceLocation CURSED_ENERGY_RL =
            ResourceLocation.fromNamespaceAndPath(JujutsuSorcery.MOD_ID, "cursed_energy");

    /**
     * Register the capability type. Called during mod construction.
     */
    @SubscribeEvent
    public static void onRegisterCapabilities(RegisterCapabilitiesEvent event) {
        event.register(ICursedEnergy.class);
    }

    /**
     * Attach CursedEnergy capability to all players.
     */
    @SubscribeEvent
    public static void onAttachCapabilitiesPlayer(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof Player) {
            if (!event.getObject().getCapability(CURSED_ENERGY_CAPABILITY).isPresent()) {
                CursedEnergyProvider provider = new CursedEnergyProvider();
                event.addCapability(CURSED_ENERGY_RL, provider);
            }
        }
    }

    /**
     * Copy capability data when player respawns (keep data on death).
     */
    @SubscribeEvent
    public static void onPlayerCloned(PlayerEvent.Clone event) {
        if (event.isWasDeath()) {
            event.getOriginal().getCapability(CURSED_ENERGY_CAPABILITY).ifPresent(oldStore -> {
                event.getEntity().getCapability(CURSED_ENERGY_CAPABILITY).ifPresent(newStore -> {
                    newStore.copyFrom(oldStore);
                });
            });
        }
    }

    // ============================================================
    // Helper Methods
    // ============================================================

    /**
     * Get the CursedEnergy capability from a player safely.
     */
    public static net.minecraftforge.common.util.LazyOptional<ICursedEnergy> get(Player player) {
        return player.getCapability(CURSED_ENERGY_CAPABILITY);
    }

    /**
     * Quick helper to get CE data or null. Use only where null is safe.
     */
    @org.jetbrains.annotations.Nullable
    public static ICursedEnergy getOrNull(Player player) {
        return player.getCapability(CURSED_ENERGY_CAPABILITY).orElse(null);
    }
}
