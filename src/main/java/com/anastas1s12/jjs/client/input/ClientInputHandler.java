package com.anastas1s12.jjs.client.input;

import com.anastas1s12.jjs.JujutsuSorcery;
import com.anastas1s12.jjs.capability.CursedEnergyCapability;
import com.anastas1s12.jjs.client.screen.menu.AbilitiesTabScreen;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Handles all game-loop input events (Key and Mouse clicks).
 * These must listen to Bus.FORGE (the default).
 */
@Mod.EventBusSubscriber(modid = JujutsuSorcery.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ClientInputHandler {

    /**
     * Handle key input events (key pressed/released).
     * This fires every time a key is pressed while in-game.
     */
    @SubscribeEvent
    public static void onKeyInput(InputEvent.Key event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return; // Must be in-game
        if (mc.screen != null) return; // Don't trigger while in a GUI

        // ---- OPEN MENU (J key) ----
        if (Keybinds.OPEN_MENU.consumeClick()) {
            openMenu();
            return;
        }

        // ---- TOGGLE RCT (R key) ----
        if (Keybinds.TOGGLE_RCT.consumeClick()) {
            toggleRCT();
            return;
        }
    }

    /**
     * Handle mouse input events (mouse buttons).
     * This fires every time a mouse button is clicked.
     */
    @SubscribeEvent
    public static void onMouseInput(InputEvent.MouseButton event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;
        if (mc.screen != null) return;

        // ---- USE ABILITY (Middle mouse) ----
        if (Keybinds.USE_ABILITY.consumeClick()) {
            useHotbarAbility();
        }
    }

    // ============================================================
    // ACTIONS
    // ============================================================

    private static void openMenu() {
        Minecraft mc = Minecraft.getInstance();
        mc.setScreen(new AbilitiesTabScreen());
    }

    private static void toggleRCT() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

        mc.player.getCapability(CursedEnergyCapability.CURSED_ENERGY_CAPABILITY).ifPresent(ce -> {
            if (ce.getMasteryLevel() < 50 && !ce.isRCTActive()) {
                mc.player.displayClientMessage(
                        net.minecraft.network.chat.Component.literal("\u00A7cRCT requires Mastery Level 50"),
                        true);
                return;
            }

            ce.setRCTActive(!ce.isRCTActive());

            String msg = ce.isRCTActive() ?
                    "\u00A7dReverse Cursed Technique: ON" :
                    "\u00A77Reverse Cursed Technique: OFF";
            mc.player.displayClientMessage(
                    net.minecraft.network.chat.Component.literal(msg), true);
        });
    }

    private static void useHotbarAbility() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

        mc.player.displayClientMessage(
                net.minecraft.network.chat.Component.literal("\u00A77Ability used!"),
                true);
    }

    /**
     * Nested class to safely capture Mod-specific startup events (Bus.MOD).
     * This satisfies Forge's rule requiring registration events to sit on the Mod Bus.
     */
    @Mod.EventBusSubscriber(modid = JujutsuSorcery.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ModBusSubscriber {
        @SubscribeEvent
        public static void onRegisterKeyMappings(RegisterKeyMappingsEvent event) {
            event.register(Keybinds.OPEN_MENU);
            event.register(Keybinds.TOGGLE_RCT);
            event.register(Keybinds.USE_ABILITY);

            JujutsuSorcery.LOGGER.info("JJS keybinds registered via Client Mod Bus.");
        }
    }
}
