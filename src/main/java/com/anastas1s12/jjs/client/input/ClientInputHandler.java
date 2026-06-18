package com.anastas1s12.jjs.client.input;

import com.anastas1s12.jjs.JujutsuSorcery;
import com.anastas1s12.jjs.client.ClientAbilityData;
import com.anastas1s12.jjs.client.ClientSorcererState;
import com.anastas1s12.jjs.client.screen.menu.AbilitiesTabScreen;
import com.anastas1s12.jjs.networking.ModNetworking;
import com.anastas1s12.jjs.networking.c2s.ToggleSorcererModeC2SPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Handles all client-side input events.
 *
 * Key bindings (Bus.FORGE):
 *   J  → open sorcerer menu
 *   R  → toggle sorcerer mode  (was: toggle RCT)
 *   Middle mouse → use selected ability
 *
 * Mouse scroll (Bus.FORGE):
 *   While sorcerer mode is active the scroll wheel cycles the ability
 *   hotbar slot instead of the vanilla item hotbar.
 */
@Mod.EventBusSubscriber(modid = JujutsuSorcery.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ClientInputHandler {

    // ── Key input ─────────────────────────────────────────────────────────────

    @SubscribeEvent
    public static void onKeyInput(InputEvent.Key event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;
        if (mc.screen != null) return; // don't fire while a GUI is open

        // ---- OPEN MENU (J) -----------------------------------------------
        if (Keybinds.OPEN_MENU.consumeClick()) {
            mc.setScreen(new AbilitiesTabScreen());
            return;
        }

        // ---- TOGGLE SORCERER MODE (R) ------------------------------------
        if (Keybinds.TOGGLE_SORCERER_MODE.consumeClick()) {
            toggleSorcererMode();
            return;
        }
    }

    // ── Mouse button input ────────────────────────────────────────────────────

    @SubscribeEvent
    public static void onMouseInput(InputEvent.MouseButton event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;
        if (mc.screen != null) return;

        // ---- USE ABILITY (middle mouse) ----------------------------------
        if (Keybinds.USE_ABILITY.consumeClick()) {
            useSelectedAbility();
        }
    }

    // ── Mouse scroll ─────────────────────────────────────────────────────────

    /**
     * While sorcerer mode is active, hijack the scroll wheel to cycle the
     * ability hotbar slot and cancel the vanilla hotbar scroll.
     */
    @SubscribeEvent
    public static void onMouseScroll(InputEvent.MouseScrollingEvent event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;
        if (mc.screen != null) return;
        if (!ClientSorcererState.isSorcererModeActive()) return;

        // delta > 0 → scroll up → move selection left (-1)
        // delta < 0 → scroll down → move selection right (+1)
        int delta = event.getScrollDelta() > 0 ? -1 : 1;
        ClientSorcererState.scrollSlot(delta);

        // Cancel the event so vanilla hotbar scroll is suppressed
        event.setCanceled(true);
    }

    // ── Actions ───────────────────────────────────────────────────────────────

    /**
     * Sends a ToggleSorcererModeC2SPacket to the server.
     * The server flips the flag and echoes back a SorcererModeSyncS2CPacket
     * which updates {@link ClientSorcererState} on the client.
     */
    private static void toggleSorcererMode() {
        ModNetworking.INSTANCE.sendToServer(new ToggleSorcererModeC2SPacket());

        // Optimistic local feedback message (server will confirm shortly)
        boolean willBeActive = !ClientSorcererState.isSorcererModeActive();
        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null) {
            String msg = willBeActive
                    ? "\u00A7aSorcerer Mode: ON"
                    : "\u00A77Sorcerer Mode: OFF";
            mc.player.displayClientMessage(Component.literal(msg), true);
        }
    }

    /**
     * Uses the ability currently in the selected hotbar slot.
     * TODO: send a UseAbilityC2SPacket when the ability execution system exists.
     */
    private static void useSelectedAbility() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;
        if (!ClientSorcererState.isSorcererModeActive()) return;

        int slot = ClientSorcererState.getSelectedSlot();
        var ability = ClientAbilityData.getSlotAbility(slot);

        if (ability == null) {
            mc.player.displayClientMessage(
                    Component.literal("\u00A77No ability in slot " + (slot + 1)), true);
            return;
        }

        // TODO: send UseAbilityC2SPacket(ability.getId()) to the server
        mc.player.displayClientMessage(
                Component.literal("\u00A7b[" + ability.getName() + "] used!"), true);
    }

    // ── Mod bus subscriber (keybind registration) ─────────────────────────────

    @Mod.EventBusSubscriber(modid = JujutsuSorcery.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ModBusSubscriber {
        @SubscribeEvent
        public static void onRegisterKeyMappings(RegisterKeyMappingsEvent event) {
            event.register(Keybinds.OPEN_MENU);
            event.register(Keybinds.TOGGLE_SORCERER_MODE);
            event.register(Keybinds.USE_ABILITY);
            JujutsuSorcery.LOGGER.info("JJS keybinds registered.");
        }
    }
}
