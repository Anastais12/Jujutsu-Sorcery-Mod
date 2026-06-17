package com.anastas1s12.jjs.client.input;

import com.anastas1s12.jjs.JujutsuSorcery;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.client.settings.KeyConflictContext;
import org.lwjgl.glfw.GLFW;

/**
 * =============================================================================
 * KEYBINDS — All mod key mappings
 * =============================================================================
 *
 * Register all keybinds here. They are registered to the Forge
 * key registry during FMLClientSetupEvent.
 *
 * Current keybinds:
 *   - OPEN_MENU: Opens the main JJS sorcerer menu (default: J)
 *   - TOGGLE_RCT: Toggles Reverse Cursed Technique (default: R)
 *   - USE_ABILITY: Uses the currently selected hotbar ability (default: Middle mouse)
 *
 * To add a new keybind:
 *   1. Add a new KeyMapping field below
 *   2. Register it in the register() method
 *   3. Handle it in ClientInputHandler
 *
 * =============================================================================
 */
public class Keybinds {

    /** Category name shown in Minecraft's Controls menu */
    public static final String CATEGORY = "key.categories." + JujutsuSorcery.MOD_ID;

    // ============================================================
    // KEY MAPPINGS
    // ============================================================

    /**
     * Opens the main Jujutsu Sorcery menu.
     * Default: J key
     */
    public static final KeyMapping OPEN_MENU = new KeyMapping(
            "key." + JujutsuSorcery.MOD_ID + ".open_menu",     // Translation key
            KeyConflictContext.IN_GAME,                         // Only works in-game
            InputConstants.Type.KEYSYM,                         // Keyboard key
            GLFW.GLFW_KEY_J,                                    // Default: J
            CATEGORY                                            // Category in controls menu
    );

    /**
     * Toggles Reverse Cursed Technique.
     * Default: R key
     */
    public static final KeyMapping TOGGLE_RCT = new KeyMapping(
            "key." + JujutsuSorcery.MOD_ID + ".toggle_rct",
            KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_R,
            CATEGORY
    );

    /**
     * Uses the currently selected hotbar ability.
     * Default: Middle mouse button
     */
    public static final KeyMapping USE_ABILITY = new KeyMapping(
            "key." + JujutsuSorcery.MOD_ID + ".use_ability",
            KeyConflictContext.IN_GAME,
            InputConstants.Type.MOUSE,
            GLFW.GLFW_MOUSE_BUTTON_MIDDLE, // Middle click
            CATEGORY
    );

    // ============================================================
    // REGISTRATION
    // ============================================================

    /**
     * Register all keybinds. Call this during FMLClientSetupEvent.
     *
     * Example in your main mod class:
     *   modEventBus.addListener(this::clientSetup);
     *
     *   private void clientSetup(FMLClientSetupEvent event) {
     *       event.enqueueWork(() -> {
     *           Keybinds.register();
     *       });
     *   }
     */
    public static void register() {
        // KeyMappings are auto-registered when created with the @SubscribeEvent
        // approach, or you can register them manually to the ClientRegistry.
        // In 1.20.1, KeyMapping instances need to be registered via event:
        //
        // @SubscribeEvent
        // public static void onRegisterKeyMappings(RegisterKeyMappingsEvent event) {
        //     event.register(Keybinds.OPEN_MENU);
        //     event.register(Keybinds.TOGGLE_RCT);
        //     event.register(Keybinds.USE_ABILITY);
        // }
        //
        // See ClientInputHandler for the event handler.
    }
}
