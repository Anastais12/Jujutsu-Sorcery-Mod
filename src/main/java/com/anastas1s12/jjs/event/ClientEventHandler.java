package com.anastas1s12.jjs.event;

import com.anastas1s12.jjs.JujutsuSorcery;
import com.anastas1s12.jjs.client.ClientSorcererState;
import com.anastas1s12.jjs.client.gui.CursedEnergyHudOverlay;
import com.anastas1s12.jjs.client.gui.SorcererHotbarOverlay;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Client-side event handler.
 *
 * Mod bus  — overlay registration.
 * Forge bus — vanilla hotbar suppression while sorcerer mode is active.
 */
public class ClientEventHandler {

    // =========================================================================
    // MOD BUS — overlay registration
    // =========================================================================

    @Mod.EventBusSubscriber(modid = JujutsuSorcery.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ModBusEvents {

        @SubscribeEvent
        public static void onRegisterGuiOverlays(RegisterGuiOverlaysEvent event) {
            // CE energy bar — rendered above everything
            event.registerAboveAll("cursed_energy_hud", CursedEnergyHudOverlay.INSTANCE);

            // Sorcerer ability hotbar — rendered above the vanilla hotbar area
            // Only actually draws when ClientSorcererState.isSorcererModeActive() == true
            event.registerAbove(VanillaGuiOverlay.HOTBAR.id(),
                    "sorcerer_hotbar", SorcererHotbarOverlay.INSTANCE);
        }
    }

    // =========================================================================
    // FORGE BUS — suppress vanilla overlays during sorcerer mode
    // =========================================================================

    @Mod.EventBusSubscriber(modid = JujutsuSorcery.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
    public static class ForgeBusEvents {

        /**
         * Cancels vanilla overlay renders while sorcerer mode is active so
         * the player only sees the ability hotbar and not the vanilla one.
         *
         * Overlays suppressed:
         *   - HOTBAR            (the 9-slot item bar)
         *   - HOTBAR_SELECTED_SLOT (the slot highlight sprite)
         *
         * Uses HIGHEST priority so we cancel before anything else runs.
         */
        @SubscribeEvent(priority = EventPriority.HIGHEST)
        public static void onPreRenderHotbar(RenderGuiOverlayEvent.Pre event) {
            if (!ClientSorcererState.isSorcererModeActive()) return;

            var id = event.getOverlay().id();

            // Cancel vanilla hotbar background render
            if (id.equals(VanillaGuiOverlay.HOTBAR.id())) {
                event.setCanceled(true);
            }
        }
    }
}
